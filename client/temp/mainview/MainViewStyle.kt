package eu.timerertim.downlomatic.graphics.mainview

import tornadofx.Dimension
import tornadofx.Dimension.LinearUnits.*
import tornadofx.FXVisibility.*
import tornadofx.Stylesheet
import tornadofx.box

/**
 * The [Stylesheet] for the [MainView].
 */
class MainViewStyle : Stylesheet() {
    init {
        columnHeaderBackground {
            visibility = HIDDEN
            padding = box(
                Dimension(.0, em),
                Dimension(.0, em),
                Dimension(-1.0, em),
                Dimension(.0, em)
            )
        }
    }
}