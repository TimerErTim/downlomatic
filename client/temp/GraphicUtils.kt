package eu.timerertim.downlomatic.graphics

import javafx.beans.property.Property
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.stage.Screen
import javafx.stage.Stage

/**
 * Provides helpful methods handling GUI stuff.
 */
object GraphicUtils {
    /**
     * Sizes the target [Stage] to be half the screen big on each dimension.
     */
    @JvmStatic
    fun Stage.defaultSize() {
        width = Screen.getPrimary().bounds.width / 2
        height = Screen.getPrimary().bounds.height / 2
    }
}