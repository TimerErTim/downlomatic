package eu.timerertim.downlomatic.core.download

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import eu.timerertim.downlomatic.api.APIPath
import eu.timerertim.downlomatic.api.APIRequest
import eu.timerertim.downlomatic.api.APIRequest.Companion.executeRequest
import eu.timerertim.downlomatic.api.APIState
import eu.timerertim.downlomatic.core.descriptor.Descriptor
import eu.timerertim.downlomatic.core.downloader.Downloader
import eu.timerertim.downlomatic.core.video.Video
import kotlinx.coroutines.*
import java.io.File

/**
 * Represents a pausable Download targeting a single file.
 */
class Download(val video: Video, val targetFile: File) {
    var state: DownloadState by mutableStateOf(DownloadState.Initial)
        private set

    val name: String get() = targetFile.name

    val size get() = downloader?.size ?: -1
    val downloadedBytes get() = downloader?.downloadedBytes ?: 0
    val totalBytes get() = downloader?.let { it.downloadedBytes + it.obsoleteBytes } ?: 0

    private var downloader: Downloader<*>? = null
    private val downloaderRequest = APIRequest<Downloader<Descriptor>>(APIPath.DOWNLOADER_OF_VIDEO)

    private val fetchingJob = CoroutineScope(Dispatchers.IO).async(start = CoroutineStart.LAZY) {
        state = DownloadState.Parsing

        downloaderRequest.executeRequest(APIPath.DOWNLOADER_OF_VIDEO.VIDEO_ID to video.idHash)

        state = when (val requestState = downloaderRequest.state) {
            is APIState.Error -> DownloadState.Error(requestState.exception)
            is APIState.Loaded -> return@async requestState.payload
            else -> DownloadState.Error(IllegalStateException("Downloader request has not finished in any way"))
        }

        return@async null
    }

    fun startDownload() {
        when (state) {
            is DownloadState.Initial, DownloadState.Paused -> CoroutineScope(Dispatchers.Default).launch {
                val downloader = fetchingJob.await()
                if (downloader != null) {
                    if (this@Download.downloader == null) {
                        downloader.initialize(targetFile)
                        this@Download.downloader = downloader
                    }

                    state = DownloadState.Transitioning

                    try {
                        downloader.startDownload()
                        state = DownloadState.Downloading

                        if (downloader.joinDownload()) {
                            state = DownloadState.Finished
                        }
                    } catch (ex: Exception) {
                        state = DownloadState.Error(ex)
                    }
                }
            }
            else -> {}
        }
    }

    fun pauseDownload() {
        if (state is DownloadState.Downloading) {
            state = DownloadState.Transitioning
            CoroutineScope(Dispatchers.Default).launch {
                state = try {
                    downloader?.pauseDownload()
                    DownloadState.Paused
                } catch (ex: Exception) {
                    DownloadState.Error(ex)
                }
            }
        }
    }

    fun stopDownload() {
        when (state) {
            is DownloadState.Error, DownloadState.Stopped, DownloadState.Finished -> {}
            else -> {
                state = DownloadState.Stopped
                CoroutineScope(Dispatchers.Default).launch {
                    try {
                        downloader?.stopDownload()
                    } catch (ex: Exception) {
                        state = DownloadState.Error(ex)
                    }
                }
            }
        }
    }

    suspend fun joinDownload(): Boolean {
        return when (state) {
            is DownloadState.Error, DownloadState.Stopped -> {
                false
            }
            is DownloadState.Finished -> {
                true
            }
            else -> {
                downloader?.joinDownload() ?: false
            }
        }
    }

    suspend fun startAndJoinDownload(): Boolean {
        startDownload()
        return joinDownload()
    }
}
