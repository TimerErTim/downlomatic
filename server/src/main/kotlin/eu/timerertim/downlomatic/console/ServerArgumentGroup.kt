package eu.timerertim.downlomatic.console

import org.apache.commons.cli.OptionGroup

/**
 * This enum stores groups of [ServerArgument], in which only one of the possible Arguments
 * may be used.
 */
enum class ServerArgumentGroup(vararg args: ServerArgument) : ArgumentGroup {
    ;

    override val optionGroup = OptionGroup()

    init {
        args.forEach { optionGroup.addOption(it.option) }
    }

    constructor(vararg args: ServerArgument, setup: OptionGroup.() -> Unit) : this(*args) {
        setup(optionGroup)
    }
}