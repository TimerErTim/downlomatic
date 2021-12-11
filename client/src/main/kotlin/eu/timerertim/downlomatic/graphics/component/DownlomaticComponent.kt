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

@Composable
fun DownlomaticWindowContent() {
    Column(verticalArrangement = Arrangement.spacedBy(5.sdp)) {
        Row(modifier = Modifier.weight(1F), horizontalArrangement = Arrangement.spacedBy(5.sdp)) {
            Box(modifier = Modifier.weight(1F)) {
                DownlomaticLeft()
            }
            Box(modifier = Modifier.weight(2.5F)) {
                DownlomaticCenter()
            }
            Box(modifier = Modifier.weight(1.5F)) {
                DownlomaticRight()
            }
        }
        Box {
            DownlomaticFooter()
        }
    }
}

@Preview
@Composable
fun DownlomaticLeft() {
    Card(elevation = 5.dp) {
        DownlomaticLeftContent()
    }
}

@Preview
@Composable
fun DownlomaticCenter() {
    Card(elevation = 5.dp) {
        DownlomaticCenterContent()
    }
}

@Preview
@Composable
fun DownlomaticRight() {
    Card(elevation = 5.dp) {
        DownlomaticRightContent()
    }
}

@Preview
@Composable
fun DownlomaticFooter() {
    Card(elevation = 5.dp) {
        DownlomaticFooterContent()
    }
}
