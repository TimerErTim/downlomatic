package eu.timerertim.downlomatic.graphics.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.timerertim.downlomatic.graphics.window.sdp
import eu.timerertim.downlomatic.state.DownloadConfigurationState
import eu.timerertim.downlomatic.state.DownloadManagerState
import eu.timerertim.downlomatic.state.DownloadStatisticsState
import eu.timerertim.downlomatic.state.DownlomaticState

@Composable
fun DownlomaticWindowContent(downlomaticState: DownlomaticState) {
    Column(verticalArrangement = Arrangement.spacedBy(5.sdp)) {
        Row(modifier = Modifier.weight(1F), horizontalArrangement = Arrangement.spacedBy(5.sdp)) {
            Box(modifier = Modifier.weight(1F)) {
                DownlomaticLeft(downlomaticState)
            }
            Box(modifier = Modifier.weight(2.5F)) {
                DownlomaticCenter(downlomaticState.downloadManagerState)
            }
            Box(modifier = Modifier.weight(1.5F)) {
                DownlomaticRight(downlomaticState.downloadConfigurationState)
            }
        }
        Box {
            DownlomaticFooter(downlomaticState.downloadStatisticsState)
        }
    }
}

@Preview
@Composable
fun DownlomaticLeft(state: DownlomaticState) {
    Card(elevation = 5.dp) {
        DownlomaticLeftContent(state)
    }
}

@Preview
@Composable
fun DownlomaticCenter(downloadManagerState: DownloadManagerState) {
    Card(elevation = 5.dp) {
        DownlomaticCenterContent(downloadManagerState)
    }
}

@Preview
@Composable
fun DownlomaticRight(configurationState: DownloadConfigurationState) {
    Card(elevation = 5.dp) {
        DownlomaticRightContent(configurationState)
    }
}

@Preview
@Composable
fun DownlomaticFooter(statisticsState: DownloadStatisticsState) {
    Card(elevation = 5.dp) {
        DownlomaticFooterContent(statisticsState)
    }
}
