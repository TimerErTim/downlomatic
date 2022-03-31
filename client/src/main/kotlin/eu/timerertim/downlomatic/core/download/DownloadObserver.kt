package eu.timerertim.downlomatic.core.download

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import eu.timerertim.downlomatic.graphics.component.util.LabeledProgressState
import eu.timerertim.downlomatic.util.Utils.toHumanReadableBytesBin
import eu.timerertim.downlomatic.util.logging.Log
import kotlinx.coroutines.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class DownloadObserver(
    val download: Download,
    val sizeDelay: Duration = 50.milliseconds,
    val speedDelay: Duration = 1.seconds,
    val onEnd: (DownloadState) -> Unit = {}
) {
    val progress = LabeledProgressState(download.downloadedBytes, download.size) { amount, max ->
        val downloadState = download.state
        if (downloadState is DownloadState.Parsing) {
            "Fetching..."
        } else if (downloadState is DownloadState.Error) {
            "Error occurred: ${downloadState.exception.message}"
        } else if (max < 0) {
            "Downloaded ${amount.toHumanReadableBytesBin()}"
        } else {
            "${amount.toHumanReadableBytesBin()} / ${max.toHumanReadableBytesBin()}"
        }
    }

    var speed by mutableStateOf(0L)
        private set
    var totalBytes by mutableStateOf(0L)
        private set

    private var size by progress::denominator
    private var downloadedBytes by progress::nominator

    @OptIn(ExperimentalTime::class)
    val job = CoroutineScope(Dispatchers.Main).launch {
        val sizeJob = launch {
            while (isActive) {
                val realDelay = measureTime {
                    size = download.size
                    downloadedBytes = download.downloadedBytes
                    totalBytes = download.totalBytes
                    delay(sizeDelay)
                }
            }
        }
        val speedJob = launch {
            var previousDownloaded = 0L
            var realDelay = speedDelay
            while (isActive) {
                realDelay = measureTime {
                    speed = ((downloadedBytes - previousDownloaded) / realDelay.toDouble(DurationUnit.SECONDS)).toLong()
                    previousDownloaded = downloadedBytes
                    delay(speedDelay)
                }
            }
        }

        val checkDelay = (if (sizeDelay < speedDelay) sizeDelay else speedDelay) / 2
        while (!download.state.let {
                it is DownloadState.Stopped
                        || it is DownloadState.Finished
                        || it is DownloadState.Error
            } && isActive) {
            delay(checkDelay)
        }

        val downloadState = download.state
        if (downloadState is DownloadState.Error) {
            Log.e(
                "Download ${download.name} with url ${download.video.url} has terminated with an error",
                downloadState.exception
            )
        }

        sizeJob.cancel()
        speedJob.cancel()

        if (isActive) {
            onEnd(download.state)
        }

        sizeJob.join()
        speedJob.join()
    }
}
