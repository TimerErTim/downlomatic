package eu.timerertim.downlomatic.console

import org.apache.commons.cli.OptionGroup

/**
 * This enum stores groups of [ServerArguments], in which only one of the possible Arguments
 * may be used.
 */
enum class ServerArgumentGroups(vararg args: ServerArguments) : ArgumentGroup {
    ;

    override val optionGroup = OptionGroup()

    init {
        args.forEach { optionGroup.addOption(it.option) }
    }

    constructor(vararg args: ServerArguments, setup: OptionGroup.() -> Unit) : this(*args) {
        setup(optionGroup)
    }
}