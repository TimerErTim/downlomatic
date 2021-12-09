package eu.timerertim.downlomatic.graphics

import eu.timerertim.downlomatic.graphics.mainview.MainView
import tornadofx.App

/*
Component
    Constraints/Behavior
    Logical design (like tooltip/placeholder)
    Controller connection and logic
    Subcomponents
        Constraints/Behavior
        Logical design
        ...
    Inline CSS Style (optional)
*/

/**
 * This class represents the GUI and provides its entry point.
 */
class GUI : App(MainView::class, Style::class)