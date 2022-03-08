package eu.timerertim.downlomatic.core.download

sealed class DownloadState {
    object Initial : DownloadState()

    object Parsing : DownloadState()

    object Downloading : DownloadState()

    object Transitioning : DownloadState()

    object Paused : DownloadState()

    object Stopped : DownloadState()

    object Finished : DownloadState()

    class Error(val exception: Exception) : DownloadState()
}
