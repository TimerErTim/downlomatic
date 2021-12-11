package eu.timerertim.downlomatic.core.download

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import eu.timerertim.downlomatic.core.video.Video
import eu.timerertim.downlomatic.graphics.component.util.LabeledProgressState
import eu.timerertim.downlomatic.util.toHumanReadableBytesBin
import java.io.File

/**
 * Represents a pausable Download targeting a single file.
 */
/*sealed*/abstract class Download(val video: Video, val targetFile: File) {
    val size by video.metadata::size
    val progressState = LabeledProgressState(-1, size) { amount, max ->
        if (amount < 0) {
            "Fetching..."
        } else {
            "${amount.toHumanReadableBytesBin()} / ${max.toHumanReadableBytesBin()}"
        }
    }
    var downloadedBytes by progressState::nominator
    var isRunning by mutableStateOf(true)
        protected set
    var speed by mutableStateOf(0L)
        protected set

    val isFinished get() = downloadedBytes == size
    val isAlreadyDownloaded by lazy {
    }

    abstract fun pauseDownload()
    abstract fun continueDownload()
    abstract fun startDownload()
    abstract fun stopDownload()
    abstract suspend fun joinDownload()
    suspend fun startAndJoinDownload() {
        startDownload()
        joinDownload()
    }
}