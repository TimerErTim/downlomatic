package eu.timerertim.downlomatic.console

import org.apache.commons.cli.OptionGroup

/**
 * This enum stores groups of [ClientArguments], in which only one of the possible Arguments
 * may be used.
 */
enum class ClientArgumentGroups(vararg args: ClientArguments) : ArgumentGroup {
    SOURCE(ClientArguments.ALL, ClientArguments.SERIES, ClientArguments.DOWNLOADER, setup = {
        isRequired = true
    });

    override val optionGroup = OptionGroup()

    init {
        args.forEach { optionGroup.addOption(it.option) }
    }

    constructor(vararg args: ClientArguments, setup: OptionGroup.() -> Unit) : this(*args) {
        setup(optionGroup)
    }
}