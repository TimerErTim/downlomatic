package eu.timerertim.downlomatic.graphics.component.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import eu.timerertim.downlomatic.graphics.window.sdp

@Composable
fun LabeledLinearProgressIndicator(
    progressState: LabeledProgressState<out Number>,
    height: Dp = 10.sdp,
    textHeight: TextUnit = height.value.sp,
    modifier: Modifier = Modifier
) {
    Row(horizontalArrangement = Arrangement.spacedBy(5.sdp), modifier = modifier) {
        val progressModifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .weight(1F)
            .align(Alignment.CenterVertically)
        val color = MaterialTheme.colors.primary
        val percentage = progressState.percentage
        if (percentage < 0) {
            LinearProgressIndicator(modifier = progressModifier, color = color)
        } else {
            LinearProgressIndicator(percentage, modifier = progressModifier, color = color)
        }

        Text(
            progressState.label, fontSize = textHeight,
            maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

class LabeledProgressState<T : Number>(
    initialNominator: T, initialDenominator: T, private val labelGenerator: (T, T) -> String
) {
    var percentage by mutableStateOf(initialNominator.toFloat() / initialDenominator.toFloat())
        private set

    var nominator: T = initialNominator
        set(value) {
            field = value
            percentage = value.toFloat() / denominator.toFloat()
            label = labelGenerator(nominator, denominator)
        }
    var denominator: T = initialDenominator
        set(value) {
            field = value
            percentage = nominator.toFloat() / value.toFloat()
            label = labelGenerator(nominator, denominator)
        }

    var label by mutableStateOf(labelGenerator(nominator, denominator))
        private set
}
