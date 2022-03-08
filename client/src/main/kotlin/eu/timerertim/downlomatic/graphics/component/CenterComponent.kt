package eu.timerertim.downlomatic.graphics.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import eu.timerertim.downlomatic.core.download.Download
import eu.timerertim.downlomatic.core.download.DownloadObserver
import eu.timerertim.downlomatic.graphics.component.custom.CurrentDownloadCard
import eu.timerertim.downlomatic.graphics.component.custom.QueuedDownloadCard
import eu.timerertim.downlomatic.graphics.component.util.ScrollableLazyColumn
import eu.timerertim.downlomatic.graphics.theme.outline
import eu.timerertim.downlomatic.graphics.window.sdp
import eu.timerertim.downlomatic.state.DownloadManagerState

@Composable
fun DownlomaticCenterContent(downloadManagerState: DownloadManagerState) {
    Column(verticalArrangement = Arrangement.spacedBy(5.sdp), modifier = Modifier.padding(5.sdp).fillMaxHeight()) {
        CurrentDownloadList(downloadManagerState.downloadPool)
        QueuedDownloadList(downloadManagerState.downloadQueue)
    }
}

@Composable
fun ColumnScope.CurrentDownloadList(downloadPool: MutableList<DownloadObserver>) {
    ScrollableLazyColumn(
        modifier = Modifier.border(1.sdp, MaterialTheme.colors.outline, MaterialTheme.shapes.medium).padding(5.sdp)
            .weight(2F)
    ) {
        itemsIndexed(downloadPool) { index, it ->
            Box(Modifier.padding(end = 4.sdp)) {
                if (index > 0) Divider(thickness = 1.sdp, modifier = Modifier.padding(end = 4.sdp))
                CurrentDownloadCard(it)
            }
        }
    }
}

@Composable
fun ColumnScope.QueuedDownloadList(downloadQueue: MutableList<Download>) {
    ScrollableLazyColumn(
        modifier = Modifier.border(1.sdp, MaterialTheme.colors.outline, MaterialTheme.shapes.medium).padding(5.sdp)
            .weight(3F)
    ) {
        itemsIndexed(downloadQueue) { index, it ->
            Box(Modifier.padding(end = 4.sdp)) {
                if (index > 0) Divider(thickness = 1.sdp, modifier = Modifier.padding(end = 4.sdp))
                QueuedDownloadCard(it, onStopClick = {
                    downloadQueue.remove(it)
                })
            }
        }
    }
}
