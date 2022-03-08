package eu.timerertim.downlomatic.graphics

import androidx.compose.ui.window.application
import eu.timerertim.downlomatic.state.GlobalDownlomaticState

object GUI {
    fun start() = application {
        DownlomaticApplication(GlobalDownlomaticState)
    }
}
