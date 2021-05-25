package eu.timerertim.downlomatic.graphics

import javafx.application.Application
import javafx.application.Platform
import javafx.stage.Stage

/**
 * This is the GUI main class.
 *
 * Handles the initialization of the GUI.
 */
class GUI : Application() {
    override fun start(primaryStage: Stage) {
        primaryStage.close()
        Platform.exit()
    }

    companion object {
        fun start(vararg args: String) {
            launch(*args)
        }
    }
}