package eu.timerertim.downlomatic.graphics.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import eu.timerertim.downlomatic.graphics.component.util.LabeledLinearProgressIndicator
import eu.timerertim.downlomatic.graphics.component.util.LabeledProgressState
import eu.timerertim.downlomatic.graphics.window.sdp
import eu.timerertim.downlomatic.util.Utils.toHumanReadableBytesBin

@Composable
fun DownlomaticFooterContent() {
    Row(
        modifier = Modifier.padding(5.sdp),
        horizontalArrangement = Arrangement.spacedBy(5.sdp),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        SpeedInformation(1032000L, modifier = Modifier.weight(1F))
        ProgressBytesInformation(modifier = Modifier.weight(4F))
        ProgressDownloadsInformation(state = LabeledProgressState(100000, 999999) { amount, of ->
            "$amount / $of Downloads"
        }, modifier = Modifier.weight(1.5F))
    }
}

@Composable
fun SpeedInformation(speed: Long, modifier: Modifier = Modifier) {
    Row(horizontalArrangement = Arrangement.spacedBy(5.sdp), modifier = modifier) {
        Text(
            "Speed:", modifier = Modifier.align(Alignment.CenterVertically),
            style = MaterialTheme.typography.body2
        )
        Text(
            "${speed.toHumanReadableBytesBin()}/s", modifier = Modifier.align(Alignment.CenterVertically),
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
fun ProgressBytesInformation(modifier: Modifier = Modifier) {
    Row(horizontalArrangement = Arrangement.spacedBy(5.sdp), modifier = modifier) {
        Text("Progress:", style = MaterialTheme.typography.body2)
        LabeledLinearProgressIndicator(
            progressState = LabeledProgressState(
                10000L,
                9999999
            ) { amount, of ->
                "${amount.toHumanReadableBytesBin()} / ${of.toHumanReadableBytesBin()}"
            }, height = 14.sdp, modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun ProgressDownloadsInformation(state: LabeledProgressState<Int>, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Text(
            state.label, modifier = Modifier.align(Alignment.CenterEnd), style = MaterialTheme.typography.body2,
            maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.End
        )
    }
}
