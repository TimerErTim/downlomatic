package eu.timerertim.downlomatic.graphics.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import eu.timerertim.downlomatic.graphics.component.custom.CurrentDownloadCard
import eu.timerertim.downlomatic.graphics.component.custom.QueuedDownloadCard
import eu.timerertim.downlomatic.graphics.component.util.ScrollableColumn
import eu.timerertim.downlomatic.graphics.downloadList
import eu.timerertim.downlomatic.graphics.theme.outline
import eu.timerertim.downlomatic.graphics.window.sdp

@Composable
fun DownlomaticCenterContent() {
    Column(verticalArrangement = Arrangement.spacedBy(5.sdp), modifier = Modifier.padding(5.sdp).fillMaxHeight()) {
        CurrentDownloadList()
        QueuedDownloadList()
    }
}

@Composable
fun ColumnScope.CurrentDownloadList() {
    ScrollableColumn(
        modifier = Modifier.border(1.sdp, MaterialTheme.colors.outline, MaterialTheme.shapes.medium).padding(5.sdp)
            .weight(2F)
    ) {
        downloadList.forEachIndexed { index, it ->
            Box(Modifier.padding(end = 4.sdp)) {
                if (index > 0) Divider(thickness = 1.sdp, modifier = Modifier.padding(end = 4.sdp))
                CurrentDownloadCard(it)
            }
        }
    }
}

@Composable
fun ColumnScope.QueuedDownloadList() {
    ScrollableColumn(
        modifier = Modifier.border(1.sdp, MaterialTheme.colors.outline, MaterialTheme.shapes.medium).padding(5.sdp)
            .weight(3F)
    ) {
        downloadList.forEachIndexed { index, it ->
            Box(Modifier.padding(end = 4.sdp)) {
                if (index > 0) Divider(thickness = 1.sdp, modifier = Modifier.padding(end = 4.sdp))
                QueuedDownloadCard(it)
            }
        }
    }
}
