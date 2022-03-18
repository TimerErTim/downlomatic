package eu.timerertim.downlomatic.graphics.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
    Box(
        modifier = Modifier.border(1.sdp, MaterialTheme.colors.outline, MaterialTheme.shapes.medium).padding(5.sdp)
            .fillMaxWidth().weight(2F)
    ) {
        if (downloadPool.isNotEmpty()) {
            ScrollableLazyColumn {
                itemsIndexed(downloadPool) { index, it ->
                    Box(Modifier.padding(end = 4.sdp)) {
                        if (index > 0) Divider(thickness = 1.sdp, modifier = Modifier.padding(end = 4.sdp))
                        CurrentDownloadCard(it)
                    }
                }
            }
        } else {
            Text(
                "Current Downloads",
                color = MaterialTheme.colors.outline, style = MaterialTheme.typography.body1,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun ColumnScope.QueuedDownloadList(downloadQueue: MutableList<Download>) {
    Box(
        modifier = Modifier.border(1.sdp, MaterialTheme.colors.outline, MaterialTheme.shapes.medium).padding(5.sdp)
            .fillMaxWidth().weight(3F)
    ) {
        if (downloadQueue.isNotEmpty()) {
            ScrollableLazyColumn {
                itemsIndexed(downloadQueue) { index, it ->
                    Box(Modifier.padding(end = 4.sdp)) {
                        if (index > 0) Divider(thickness = 1.sdp, modifier = Modifier.padding(end = 4.sdp))
                        QueuedDownloadCard(it, onStopClick = {
                            downloadQueue.remove(it)
                        })
                    }
                }
            }
        } else {
            Text(
                "Queued Downloads",
                color = MaterialTheme.colors.outline, style = MaterialTheme.typography.body1,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
