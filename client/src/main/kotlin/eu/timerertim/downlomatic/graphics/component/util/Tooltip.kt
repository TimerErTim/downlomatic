package eu.timerertim.downlomatic.graphics.component.util

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eu.timerertim.downlomatic.graphics.component.util.TooltipType.DETAIL
import eu.timerertim.downlomatic.graphics.component.util.TooltipType.INFO
import eu.timerertim.downlomatic.graphics.theme.surfaceVariant
import eu.timerertim.downlomatic.graphics.window.sdp

@Composable
fun TooltipCard(value: String, maxWidth: Dp = 350.sdp, type: TooltipType = DETAIL) {
    Card(
        elevation = 5.dp,
        backgroundColor = when (type) {
            DETAIL -> MaterialTheme.colors.surfaceVariant
            INFO -> MaterialTheme.colors.surface
        },
        modifier = Modifier.widthIn(max = maxWidth)
    ) {
        Text(
            value, style = MaterialTheme.typography.caption, modifier = Modifier.padding(2.sdp), color = when (type) {
                DETAIL -> Color.Unspecified
                INFO -> MaterialTheme.colors.onSurface
            }
        )
    }
}

enum class TooltipType {
    INFO, DETAIL
}
