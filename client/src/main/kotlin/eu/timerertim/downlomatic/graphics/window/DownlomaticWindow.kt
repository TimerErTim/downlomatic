package eu.timerertim.downlomatic.graphics.window

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.window.Window
import eu.timerertim.downlomatic.util.ClientUtils

@Composable
fun DownlomaticWindow(content: @Composable BoxScope.() -> Unit) {
    Window(title = "Downlomatic", state = DownlomaticWindowState, onCloseRequest = ClientUtils::exit) {
        val focusManager = LocalFocusManager.current



        Box(modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
            .background(MaterialTheme.colors.background)
            .padding(5.sdp),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}