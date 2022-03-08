package eu.timerertim.downlomatic.state

import eu.timerertim.downlomatic.core.download.Download
import eu.timerertim.downlomatic.core.download.DownloadObserver
import eu.timerertim.downlomatic.core.download.DownloadState
import eu.timerertim.downlomatic.core.download.HTTPDownload
import eu.timerertim.downlomatic.core.format.VideoDetailsFormatter
import eu.timerertim.downlomatic.core.video.Video
import java.io.File

open class DownlomaticState {
    val downloadConfigurationState = DownloadConfigurationState()
    val downloadSelectionState = DownloadSelectionState()
    val downloadManagerState = DownloadManagerState()
    val downloadStatisticsState = DownloadStatisticsState(
        ::calculateOverallSpeed,
        ::calculateOverallDownloaded,
        downloadManagerState.finishedDownloadsState::value,
        ::calculateOverallPlannedDownloads
    )

    private var finishedDownloadedBytes = 0L

    fun enqueueDownload(video: Video) {
        val pathFormatter = VideoDetailsFormatter(downloadConfigurationState.videoFormatOrDefault)

        val directory = File(downloadConfigurationState.destinationDirectoryOrDefault)
        val path = pathFormatter.format(video.details)
        val target = directory
            .resolve(path)

        lateinit var refill: (Download) -> Unit

        fun startDownload(download: Download) {
            download.startDownload()
            downloadManagerState.downloadPool.add(DownloadObserver(download, onEnd = {
                if (it is DownloadState.Finished) downloadManagerState.finishedDownloads++
                finishedDownloadedBytes += download.downloadedBytes
                refill(download)
            }))
        }

        refill = { download ->
            downloadManagerState.downloadPool.removeIf { it.download === download }
            downloadManagerState.downloadQueue.firstOrNull { it.video.url.host == download.video.url.host }
                ?.let { newDownload ->
                    downloadManagerState.downloadQueue.remove(newDownload)
                    startDownload(newDownload)
                }
        }

        val download = HTTPDownload(video, target)
        downloadManagerState.downloadPool.firstOrNull { it.download.video.url.host == video.url.host }?.let {
            downloadManagerState.downloadQueue += download
        } ?: startDownload(download)
    }

    private fun calculateOverallSpeed() = downloadManagerState.downloadPool.sumOf { it.speed }

    private fun calculateOverallDownloaded() = finishedDownloadedBytes +
            downloadManagerState.downloadPool.sumOf { it.progress.nominator }

    private fun calculateOverallPlannedDownloads() = downloadManagerState.downloadPool.size +
            downloadManagerState.downloadQueue.size +
            downloadManagerState.finishedDownloads
}
