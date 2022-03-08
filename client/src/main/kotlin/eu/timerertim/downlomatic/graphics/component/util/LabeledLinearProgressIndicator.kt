package eu.timerertim.downlomatic.graphics.component.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
        val max = progressState.denominator.toDouble()
        if (max < 0) {
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
    initialNominator: T, initialDenominator: T,
    private val nominatorGenerator: (() -> T)? = null,
    private val denominatorGenerator: (() -> T)? = null,
    private val labelGenerator: (T, T) -> String
) {

    val percentage by derivedStateOf { nominatorState.toFloat() / denominatorState.toFloat() }

    var nominator: T by mutableStateOf(initialNominator)
    var denominator: T by mutableStateOf(initialDenominator)

    private val nominatorState: T by derivedStateOf(nominatorGenerator ?: { nominator })
    private val denominatorState: T by derivedStateOf(denominatorGenerator ?: { denominator })

    val label get() = labelGenerator(nominatorState, denominatorState)
}
