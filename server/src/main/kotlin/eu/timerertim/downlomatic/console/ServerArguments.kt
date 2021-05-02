package eu.timerertim.downlomatic.console

import org.apache.commons.cli.Option

/**
 * This enum stores every possible argument passable via CLI on the server program.
 */
enum class ServerArguments(override val option: Option, override val isHidden: Boolean = false) : Argument {
    ;

    constructor(option: Option, isHidden: Boolean, setup: Option.() -> Unit) : this(option, isHidden) {
        setup(this.option)
    }

    constructor(option: Option, setup: Option.() -> Unit) : this(option, false, setup)
}