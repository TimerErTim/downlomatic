package eu.timerertim.downlomatic.console

import org.apache.commons.cli.OptionGroup

/**
 * This enum stores groups of [Arguments], in which only one of the possible Arguments
 * may be used.
 */
enum class ArgumentGroups(vararg args: Arguments) {
    SOURCE({ it.isRequired = true }, Arguments.ALL, Arguments.SERIES, Arguments.DOWNLOADER);

    val optionGroup = OptionGroup()

    init {
        args.forEach { optionGroup.addOption(it.option) }
    }

    constructor(setup: (OptionGroup) -> Unit, vararg args: Arguments) : this(*args) {
        setup(optionGroup)
    }
}