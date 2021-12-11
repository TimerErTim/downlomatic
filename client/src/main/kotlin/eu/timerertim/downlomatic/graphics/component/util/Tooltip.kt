package eu.timerertim.downlomatic.graphics.component.util

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eu.timerertim.downlomatic.graphics.theme.surfaceVariant
import eu.timerertim.downlomatic.graphics.window.sdp

@Composable
fun TooltipCard(value: String, maxWidth: Dp = 350.sdp) {
    Card(
        elevation = 5.dp,
        backgroundColor = MaterialTheme.colors.surfaceVariant,
        modifier = Modifier.widthIn(max = maxWidth)
    ) {
        Text(value, style = MaterialTheme.typography.caption, modifier = Modifier.padding(2.sdp))
    }
}
