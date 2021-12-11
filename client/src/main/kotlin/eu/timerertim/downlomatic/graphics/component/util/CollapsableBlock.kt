package eu.timerertim.downlomatic.graphics.component.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import eu.timerertim.downlomatic.graphics.theme.icons
import eu.timerertim.downlomatic.graphics.window.sdp

@Composable
fun CollapsableBlock(
    expanded: Boolean = remember { mutableStateOf(false) }.value,
    onExpandedChange: (Boolean) -> Unit = { },
    modifier: Modifier = Modifier,
    leadingLabel: @Composable() (RowScope.(Boolean) -> Unit)? = null,
    trailingLabel: @Composable() (RowScope.(Boolean) -> Unit)? = null,
    content: @Composable() (AnimatedVisibilityScope.() -> Unit)
) {
    Column(modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(5.sdp)) {
            val iconRotation by animateFloatAsState(if (expanded) -180F else 0F)

            leadingLabel?.invoke(this, expanded)
            Icon(
                MaterialTheme.icons.ExpandMore, "Expandable Arrow",
                modifier = Modifier.rotate(iconRotation).size(15.sdp).clip(CircleShape)
                    .align(Alignment.Bottom).clickable(onClick = {
                        onExpandedChange(!expanded)
                    })
            )
            trailingLabel?.invoke(this, expanded)
        }
        AnimatedVisibility(expanded, content = content)
    }
}
