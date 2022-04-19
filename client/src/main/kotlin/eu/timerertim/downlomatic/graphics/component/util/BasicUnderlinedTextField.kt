package eu.timerertim.downlomatic.graphics.component.util

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import eu.timerertim.downlomatic.graphics.theme.outline
import eu.timerertim.downlomatic.graphics.window.sdp

@Composable
fun (@Composable () -> Unit).decorateUnderlined(
    value: TextFieldValue,
    tint: Color,
    placeholder: @Composable (() -> Unit)?,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(start = 5.sdp, top = 5.sdp, end = 5.sdp, bottom = 3.sdp)
        ) {
            val prevContentColor = LocalContentColor.current
            CompositionLocalProvider(LocalContentColor provides tint) {
                Row(horizontalArrangement = Arrangement.spacedBy(5.sdp), modifier = Modifier.weight(1F)) {
                    leadingIcon?.invoke()
                    Box {
                        if (value.text.isEmpty()) {
                            CompositionLocalProvider(LocalContentColor provides prevContentColor) {
                                placeholder?.invoke()
                            }
                        }
                        this@decorateUnderlined()
                    }
                }
                trailingIcon?.invoke()
            }
        }
        Divider(color = tint, thickness = 1.sdp)
    }
}

@Composable
fun BasicUnderlinedTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle.Default,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    cursorBrush: Brush = SolidColor(Color.Black)
) {
    val outlineColor = MaterialTheme.colors.outline
    val primaryColor = MaterialTheme.colors.primary
    val errorColor = MaterialTheme.colors.error
    var targetColor by remember { mutableStateOf(outlineColor) }
    val color by animateColorAsState(if (isError) errorColor else targetColor)

    BasicTextField(
        value,
        onValueChange,
        Modifier.onFocusChanged {
            targetColor = if (it.hasFocus) primaryColor else outlineColor
        } then modifier,
        enabled,
        readOnly,
        textStyle,
        keyboardOptions,
        keyboardActions,
        singleLine,
        maxLines,
        visualTransformation,
        onTextLayout,
        interactionSource,
        cursorBrush,
        decorationBox = {
            it.decorateUnderlined(value, color, placeholder, leadingIcon, trailingIcon)
        })
}

@Composable
fun BasicUnderlinedTextField(
    value: String?,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle.Default,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    cursorBrush: Brush = SolidColor(Color.Black),
    shape: Shape = MaterialTheme.shapes.small
) {
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value ?: "")) }
    val textFieldValue = textFieldValueState.copy(text = value ?: "")

    BasicUnderlinedTextField(
        textFieldValue,
        onValueChange = {
            textFieldValueState = it
            if (value != it.text) {
                onValueChange(it.text)
            }
        },
        modifier,
        enabled,
        readOnly,
        textStyle,
        placeholder,
        leadingIcon,
        trailingIcon,
        isError,
        keyboardOptions,
        keyboardActions,
        singleLine,
        maxLines,
        visualTransformation,
        onTextLayout,
        interactionSource,
        cursorBrush
    )
}
