package eu.timerertim.downlomatic.graphics

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.window.application
import eu.timerertim.downlomatic.core.download.Download
import eu.timerertim.downlomatic.core.meta.Metadata
import eu.timerertim.downlomatic.core.meta.VideoDetails
import eu.timerertim.downlomatic.core.video.Video
import eu.timerertim.downlomatic.state.GlobalDownlomaticState
import kotlinx.coroutines.*
import java.io.File
import java.net.URL
import kotlin.random.Random
import kotlin.system.measureTimeMillis

val downloadList = mutableStateListOf<Download>(
    object : Download(
        Video(
            URL("https://timerertim.eu"),
            VideoDetails(title = "Target which is very very very long Target which is very very very long Target which is very very very long"),
            Metadata(size = 2000000000L)
        ),
        File("path/to/target_which_is_very_long_to_test_overflow_of_text_target_which_is_.mp4")
    ) {
        var bytes = 0L

        val job = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                if (Random.nextDouble() < 0.005) break
                delay((Random.nextDouble() * 20).toLong())
            }
            launch(Dispatchers.Main) {
                var prevDownloaded = 0L
                var duration = 25L
                while (downloadedBytes < size && isActive) {
                    duration = measureTimeMillis {
                        downloadedBytes = bytes
                        val dif = downloadedBytes - prevDownloaded
                        speed = dif * duration
                        prevDownloaded = bytes
                        delay(25)
                    }
                }
            }

            while (bytes < size && isActive) {
                bytes += (bytesPerTick * Random.nextDouble() * 2).toLong()
            }
        }
        var bytesPerTick = 1F

        override fun pauseDownload() {
            bytesPerTick = 0F
            isRunning = false
        }

        override fun continueDownload() {
            bytesPerTick = 1024F
            isRunning = true
        }

        override fun startDownload() {
            job.start()
        }

        override fun stopDownload() {
            downloadedBytes = 0
        }

        override suspend fun joinDownload() {
            TODO("Not yet implemented")
        }

        init {
            downloadedBytes = -1
            startDownload()
        }
    }
).apply {
    val first = first()
    repeat(15) {
        this += first
    }
}

object GUI {
    fun start() = application {
        DownlomaticApplication(GlobalDownlomaticState)
    }
}
