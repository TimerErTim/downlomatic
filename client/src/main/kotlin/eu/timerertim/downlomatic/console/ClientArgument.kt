package eu.timerertim.downlomatic.console

import org.apache.commons.cli.Option

/**
 * This enum stores every possible argument passable via CLI on the client program.
 *
 *
 * Under the hood, it works with [Option]s.
 */
enum class ClientArgument(override val option: Option, override val isHidden: Boolean = false) : Argument {
    HELP(Option(null, "help", false, "shows this")),
    VERBOSE(Option("v", "verbose", false, "enables all levels of log messages")),
    NO_FILE_LOGGING(Option(null, "no-file-logging", false, "disables logging to file"), true),
    DESTINATION(Option("d", "destination", true, "where to download the videos to"), {
        argName = "directory "
    }),
    NSFW(Option("x", "nsfw", false, "display NSFW hosts")),
    PORT(Option(null, "port", true, "on what port of the server to connect to"), true, {
        argName = "port"
    }),
    SERVER(Option(null, "server", true, "what server to connect to"), true, {
        argName = "server"
    }),
    NO_GUI(Option(null, "no-gui", false, "forces cli only mode"));

    constructor(option: Option, hidden: Boolean, setup: Option.() -> Unit) : this(option, hidden) {
        setup(this.option)
    }

    constructor(option: Option, setup: Option.() -> Unit) : this(option, false, setup)

    override fun toString(): String = option.longOpt ?: option.opt
}
