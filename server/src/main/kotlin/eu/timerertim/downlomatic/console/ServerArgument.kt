package eu.timerertim.downlomatic.console

import org.apache.commons.cli.Option

/**
 * This enum stores every possible argument passable via CLI on the server program.
 */
enum class ServerArgument(override val option: Option, override val isHidden: Boolean = false) : Argument {
    HELP(Option(null, "help", false, "shows this")),
    VERBOSE(Option("v", "verbose", false, "enables all levels of log messages")),
    NO_FILE_LOGGING(Option(null, "no-file-logging", false, "disables file logging"), true),
    IGNORE_REDUNDANCY(
        Option(
            null, "ignore-redundancy", false,
            "prevents the server from removing redundant data and using already available data"
        )
    ),
    PORT(Option(null, "port", true, "on what port to listen"), {
        argName = "port"
    });

    constructor(option: Option, isHidden: Boolean, setup: Option.() -> Unit) : this(option, isHidden) {
        setup(this.option)
    }

    constructor(option: Option, setup: Option.() -> Unit) : this(option, false, setup)

    override fun toString(): String = option.longOpt ?: option.opt
}
