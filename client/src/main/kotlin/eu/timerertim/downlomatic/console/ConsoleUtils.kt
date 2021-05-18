package eu.timerertim.downlomatic.console

import eu.timerertim.downlomatic.core.format.VideoDetailsFormatter

/**
 * Provides helpful methods for CLI and terminal interaction. This special implementation
 * handles the client program.
 */
object ConsoleUtils : Console(
    ConsoleConfig(
        ClientArguments.values(),
        ClientArgumentGroups.values(),
        "\"downlomatic -d <directory> -h <host> -a | -b <url> | -s <url> " +
                "[-f <formatting>] [--subdir-format <formatting>] [-t <amount>]\"",
        "",
        "Formatting follows the rules described under the following URL: ${VideoDetailsFormatter.WIKI_URL}"
    )
)