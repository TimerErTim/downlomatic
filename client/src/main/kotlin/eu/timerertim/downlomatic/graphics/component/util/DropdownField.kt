package eu.timerertim.downlomatic.graphics.component.util

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import eu.timerertim.downlomatic.graphics.theme.icons
import eu.timerertim.downlomatic.graphics.theme.outline
import eu.timerertim.downlomatic.graphics.window.sdp
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun <T> DropdownField(
    values: List<T>,
    value: T? = null,
    onValueChanged: (T) -> Unit,
    prompt: @Composable () -> Unit = { Text("Select Entry") },
    modifier: Modifier = Modifier,
    selectedRenderer: @Composable (T) -> Unit,
    menuRenderer: @Composable (T) -> Unit = selectedRenderer
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()
    var width by remember { mutableStateOf(0) }
    val color by animateColorAsState(if (expanded) MaterialTheme.colors.primary else MaterialTheme.colors.outline)

    Box(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.onPointerEvent(PointerEventType.Press) {
                val offset = it.changes.firstOrNull()?.position ?: Offset.Zero
                scope.launch {
                    val press = PressInteraction.Press(offset)
                    interactionSource.emit(press)
                    interactionSource.emit(PressInteraction.Release(press))
                }
                expanded = true
            }.onSizeChanged { width = it.width }
                .fillMaxWidth()
                .indication(interactionSource, LocalIndication.current)
                .hoverable(interactionSource)
                .border(1.sdp, color, MaterialTheme.shapes.small)
                .padding(end = 5.sdp)
        ) {
            Box(modifier = Modifier.padding(start = 8.sdp, top = 5.sdp, bottom = 5.sdp)) {
                if (value == null) {
                    prompt()
                } else {
                    selectedRenderer(value)
                }
            }
            val iconRotation by animateFloatAsState(if (expanded) -180F else 0F)
            Icon(
                MaterialTheme.icons.ArrowDropDown,
                "Dropdown Arrow",
                tint = color,
                modifier = Modifier.size(20.sdp).align(Alignment.CenterVertically).rotate(iconRotation)
            )
        }

        DropdownMenu(expanded, onDismissRequest = {
            expanded = false
        }, modifier = Modifier.width(width.dp)) {
            for (index in values.indices) {
                val entry = values[index]
                if (index > 0) {
                    Divider(thickness = 1.sdp)
                }
                DropdownMenuItem(onClick = {
                    onValueChanged(entry)
                    expanded = false
                }) {
                    menuRenderer(entry)
                }
            }
        }
    }
}