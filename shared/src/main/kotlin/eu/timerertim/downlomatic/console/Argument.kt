package eu.timerertim.downlomatic.console

import org.apache.commons.cli.Option

/**
 * This represents a single argument/option passable to the program via CLI.
 */
interface Argument {
    val option: Option

    /**
     * Whether the options should be listed in the help screen.
     */
    val isHidden: Boolean
}
