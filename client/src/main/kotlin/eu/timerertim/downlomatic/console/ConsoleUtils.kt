package eu.timerertim.downlomatic.console

import eu.timerertim.downlomatic.core.format.VideoDetailsFormatter

/**
 * Provides helpful methods for CLI and terminal interaction. This special implementation
 * handles the client program.
 */
object ConsoleUtils : Console(
    ConsoleConfig(
        ClientArgument.values(),
        ClientArgumentGroup.values(),
        "Downlomatic [-d <directory>] [-x]",
        "",
        "Formatting follows the rules described under the following URL: ${VideoDetailsFormatter.WIKI_URL}"
    )
)
