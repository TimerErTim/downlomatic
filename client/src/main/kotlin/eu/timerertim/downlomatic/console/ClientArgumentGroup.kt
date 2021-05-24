package eu.timerertim.downlomatic.console

import org.apache.commons.cli.OptionGroup

/**
 * This enum stores groups of [ClientArgument], in which only one of the possible Arguments
 * may be used.
 */
enum class ClientArgumentGroup(vararg args: ClientArgument) : ArgumentGroup {
    SOURCE(ClientArgument.ALL, ClientArgument.SERIES, ClientArgument.DOWNLOADER, setup = {
        isRequired = true
    });

    override val optionGroup = OptionGroup()

    init {
        args.forEach { optionGroup.addOption(it.option) }
    }

    constructor(vararg args: ClientArgument, setup: OptionGroup.() -> Unit) : this(*args) {
        setup(optionGroup)
    }
}