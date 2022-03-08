package eu.timerertim.downlomatic.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import eu.timerertim.downlomatic.core.download.Download
import eu.timerertim.downlomatic.core.download.DownloadObserver

class DownloadManagerState {
    val downloadQueue = mutableStateListOf<Download>()
    val downloadPool = mutableStateListOf<DownloadObserver>()

    val finishedDownloadsState = mutableStateOf(0L)
    var finishedDownloads by finishedDownloadsState
}
