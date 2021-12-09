package eu.timerertim.downlomatic.graphics

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.singleWindowApplication
import kotlinx.coroutines.launch

object GUI {
    fun start() = singleWindowApplication(title = "Downlomatic") {

    }

    @Composable
    fun LinearProgressIndicatorLabeled(
        progressState: ProgressIndicatorLabeledState<out Number>,
        labelAlignment: Alignment.Horizontal = Alignment.End
    ) {
        val modifier = Modifier.fillMaxWidth() then Modifier.height(10.dp) then
                Modifier.padding(horizontal = 5.dp) then Modifier.clip(CircleShape)
        val color = Color.Red
        val percentage = progressState.percentage
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(progressState.label, modifier = Modifier.padding(horizontal = 5.dp).align(labelAlignment))
            if (percentage < 0) {
                LinearProgressIndicator(modifier = modifier, color = color)
            } else {
                LinearProgressIndicator(
                    percentage, modifier = modifier, color = color
                )
            }
        }
    }

    @Preview
    @Composable
    fun button() {
        val scope = rememberCoroutineScope()
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Button(
                onClick = {
                    scope.run {
                        launch {

                        }
                    }
                },
                modifier = Modifier.padding(5.dp)
            ) {
                Text("Click me!")
                Icon(Icons.Default.Delete, "Delete")
            }

            Button(
                onClick = {
                    println("Increased")
                },
                modifier = Modifier.padding(5.dp)
            ) {
                Icon(Icons.Default.Add, "Increase")
                Text("Increase max!")
            }
        }
    }
}

class ProgressIndicatorLabeledState<T : Number>(
    initialAmount: T, initialOf: T, private val labelGenerator: (T, T, Float) -> String
) {
    val percentageState: MutableState<Float> = mutableStateOf(initialAmount.toFloat() / initialOf.toFloat())
    var percentage by percentageState
        private set

    var amount: T = initialAmount
        set(value) {
            field = value
            percentage = value.toFloat() / of.toFloat()
            label = labelGenerator(amount, of, percentage)
        }
    var of: T = initialOf
        set(value) {
            field = value
            percentage = amount.toFloat() / value.toFloat()
            label = labelGenerator(amount, of, percentage)
        }

    val labelState: MutableState<String> = mutableStateOf(labelGenerator(amount, of, percentage))
    var label by labelState
}
