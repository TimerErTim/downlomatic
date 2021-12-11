package eu.timerertim.downlomatic.graphics.component.custom

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import eu.timerertim.downlomatic.core.download.Download
import eu.timerertim.downlomatic.graphics.component.util.CollapsableBlock
import eu.timerertim.downlomatic.graphics.component.util.LabeledLinearProgressIndicator
import eu.timerertim.downlomatic.graphics.component.util.TooltipCard
import eu.timerertim.downlomatic.graphics.theme.icons
import eu.timerertim.downlomatic.graphics.window.sdp
import eu.timerertim.downlomatic.util.toHumanReadableBytesBin

@Composable
fun CurrentDownloadCard(
    download: Download,
    onStopClick: () -> Unit = { },
    expandedState: MutableState<Boolean> = remember { mutableStateOf(false) }
) {
    Row(modifier = Modifier.fillMaxWidth().padding(5.sdp), horizontalArrangement = Arrangement.spacedBy(5.sdp)) {
        val (onClick, buttonContent) = if (download.isRunning) {
            {
                download.pauseDownload()
            } to (MaterialTheme.icons.Pause to "Pause")
        } else {
            {
                download.continueDownload()
            } to (MaterialTheme.icons.PlayArrow to "Continue")
        }
        val (icon, description) = buttonContent
        Icon(
            icon, description,
            modifier = Modifier.requiredSize(22.sdp).align(Alignment.CenterVertically).clip(CircleShape)
                .clickable(onClick = onClick)
        )

        Column(modifier = Modifier.weight(1F), verticalArrangement = Arrangement.spacedBy(5.sdp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                DownloadInfo(download, expandedState, modifier = Modifier.weight(1F))
                Spacer(modifier = Modifier.width(5.sdp))
                Text("${download.speed.toHumanReadableBytesBin()}/s", style = MaterialTheme.typography.caption)
            }
            LabeledLinearProgressIndicator(
                download.progressState,
                height = 10.sdp
            )
        }

        Icon(MaterialTheme.icons.Stop, "Stop",
            modifier = Modifier.requiredSize(22.sdp).align(Alignment.CenterVertically).clip(CircleShape)
                .clickable {
                    download.stopDownload()
                    onStopClick()
                })
    }
}

@Composable
fun QueuedDownloadCard(
    download: Download,
    onStopClick: () -> Unit = { },
    expandedState: MutableState<Boolean> = remember { mutableStateOf(false) }
) {
    Row(modifier = Modifier.fillMaxWidth().padding(5.sdp), horizontalArrangement = Arrangement.SpaceBetween) {
        DownloadInfo(download, expandedState, modifier = Modifier.weight(1F))
        Spacer(modifier = Modifier.width(5.sdp))
        Icon(MaterialTheme.icons.Clear, "Remove",
            modifier = Modifier.requiredSize(22.sdp).align(Alignment.CenterVertically).clip(CircleShape)
                .clickable {
                    download.stopDownload()
                    onStopClick()
                })
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DownloadInfo(
    download: Download,
    expandedState: MutableState<Boolean> = remember { mutableStateOf(false) },
    modifier: Modifier = Modifier
) {
    val (expanded, setExpanded) = expandedState
    CollapsableBlock(leadingLabel = {
        TooltipArea(tooltip = {
            TooltipCard(download.video.details.title ?: "Example Title", maxWidth = 700.sdp)
        }, modifier = Modifier.weight(1F)) {
            Text(
                download.video.details.title ?: "Example Title",
                style = MaterialTheme.typography.caption,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }, content = {
        var overflow by remember { mutableStateOf(false) }

        TooltipArea(tooltip = {
            TooltipCard(download.targetFile.path, maxWidth = 700.sdp)
        }) {
            Row(horizontalArrangement = Arrangement.spacedBy(5.sdp)) {
                Text(
                    download.targetFile.path,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    softWrap = false,
                    style = MaterialTheme.typography.caption,
                    onTextLayout = {
                        overflow = it.hasVisualOverflow
                    },
                    modifier = Modifier.weight(1F)
                )
                if (overflow) Text("...", maxLines = 1, style = MaterialTheme.typography.caption)
            }
        }
    }, expanded = expanded, onExpandedChange = setExpanded, modifier = modifier)
}
