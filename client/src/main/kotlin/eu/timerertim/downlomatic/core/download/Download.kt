package eu.timerertim.downlomatic.core.download

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import eu.timerertim.downlomatic.core.video.Video
import kotlinx.coroutines.*
import java.io.File
import java.net.URL

/**
 * Represents a pausable Download targeting a single file.
 */
sealed class Download(val video: Video, val targetFile: File) {
    var state: DownloadState by mutableStateOf(DownloadState.Initial)
        protected set

    val name: String = targetFile.name

    var size = -1L
    var downloadedBytes = 0L

    protected lateinit var url: URL
    protected lateinit var fileType: String

    private val fetchingJob = scope.async(start = CoroutineStart.LAZY) {
        state = DownloadState.Parsing

        val url = video.url
        val fileType = url.path.split(".").takeIf { it.singleOrNull() != null }?.last() ?: "mp4"
        Pair(url, fileType)
    }

    fun startDownload() {
        when (state) {
            is DownloadState.Initial, DownloadState.Paused -> CoroutineScope(Dispatchers.Default).launch {
                val (url, fileType) = fetchingJob.await()
                this@Download.url = url
                this@Download.fileType = fileType

                state = DownloadState.Transitioning
                withContext(Dispatchers.IO) {
                    try {
                        _startDownload()
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
                try {
                    _pauseDownload()
                } catch (ex: Exception) {
                    state = DownloadState.Error(ex)
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
                        _stopDownload()
                    } catch (ex: Exception) {
                        state = DownloadState.Error(ex)
                    }
                }
            }
        }
    }

    suspend fun startAndJoinDownload() {
        startDownload()
        joinDownload()
    }

    abstract suspend fun joinDownload()
    protected abstract suspend fun _startDownload()
    protected abstract suspend fun _pauseDownload()
    protected abstract suspend fun _stopDownload()

    companion object {
        @OptIn(DelicateCoroutinesApi::class)
        val context = newSingleThreadContext("Fetcher")
        val scope = CoroutineScope(context)
    }
}
