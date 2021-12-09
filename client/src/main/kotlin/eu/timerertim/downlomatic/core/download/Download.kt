package eu.timerertim.downlomatic.core.download

import eu.timerertim.downlomatic.core.video.Video
import java.io.File

/**
 * Represents a pausable Download targeting a single file.
 */
sealed class Download(val video: Video, val targetFile: File) {

    val size by video.metadata::size
    val downloadedBytesProperty = lazy { 0L }
    val downloadedBytes by downloadedBytesProperty

    val isFinished get() = downloadedBytes == size
    val isAlreadyDownloaded by lazy {
    }

    abstract fun startDownload()
    abstract fun awaitDownload()
    fun startBlocking() {
        startDownload()
        awaitDownload()
    }
}