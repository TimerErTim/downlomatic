package eu.timerertim.downlomatic.console

//import eu.timerertim.downlomatic.pages.Hosts
import org.apache.commons.cli.Option

/**
 * This enum stores every possible argument passable via CLI on the client program.
 *
 *
 * Under the hood, it works with [Option]s.
 */
enum class ClientArgument(override val option: Option, override val isHidden: Boolean = false) : Argument {
    NO_FILE_LOGGING(Option(null, "no-file-logging", false, "disables logging to file"), true),
    VERBOSE(
        Option(
            "v",
            "verbose",
            false,
            "enables verbose console logging for debugging purposes"
        ), true
    ),
    DESTINATION_DIRECTORY(
        Option("d", "destination", true, "the download destination folder"), {
            isRequired = true
            argName = "directory"
        }),
    HOST(Option("h", "host", true, "the host to download from:"), {
        isRequired = true
        argName = "host"

        // List HOSTS available
        /*
        val values: Array<Hosts> = Hosts.values()
        for (i in values.indices) {
            description += (if (i > 0) ", " else " ") + values[i].name()
        }
        */
    }),
    SERIES(Option("s", "series", true, "the full URL to a series"), {
        argName = "url"
    }),
    DOWNLOADER(Option("b", "download", true, "the full URL to a single video"), {
        argName = "url"
    }),
    ALL(Option("a", "all", false, "download every single video from the host")),
    MAX_DOWNLOADS(Option("t", "threads", true, "the maximum amount of downloads being executed at the same time"),
        { argName = "amount" }),
    DOWNLOAD_FORMAT(Option("f", "format", true, "the format of every single downloaded video"),
        { argName = "formatting" }),
    SUBDIR_FORMAT(Option(
        null, "subdir-format", true, """the format of every subdirectory created
by default, subdirectories will only be created with the --${ALL.option.longOpt} flag"""
    ),
        { argName = "formatting" }),
    NSFW(
        Option(
            "x", "nsfw", false, """
     display NSFW hosts in GUI
     if you want to show GUI you need pass only this or no argument
     """.trimIndent()
        )
    ),
    HELP(Option(null, "help", false, "shows this"));

    constructor(option: Option, hidden: Boolean, setup: Option.() -> Unit) : this(option, hidden) {
        setup(this.option)
    }

    constructor(option: Option, setup: Option.() -> Unit) : this(option, false, setup)
}