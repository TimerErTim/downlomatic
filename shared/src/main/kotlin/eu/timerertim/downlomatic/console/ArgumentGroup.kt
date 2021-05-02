package eu.timerertim.downlomatic.console

import org.apache.commons.cli.OptionGroup

/**
 * This stores groups of [Argument]s, in which only one of the possible Arguments
 * may be used.
 */
interface ArgumentGroup {
    val optionGroup: OptionGroup
}