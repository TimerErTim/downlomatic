package eu.timerertim.downlomatic.graphics

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ApplicationScope
import eu.timerertim.downlomatic.graphics.component.DownlomaticWindowContent
import eu.timerertim.downlomatic.graphics.theme.DownlomaticTheme
import eu.timerertim.downlomatic.graphics.window.DownlomaticWindow
import eu.timerertim.downlomatic.state.DownlomaticState

@Composable
fun ApplicationScope.DownlomaticApplication(downlomaticState: DownlomaticState) {
    DownlomaticTheme {
        DownlomaticWindow {
            DownlomaticWindowContent(downlomaticState)
        }
    }
}
