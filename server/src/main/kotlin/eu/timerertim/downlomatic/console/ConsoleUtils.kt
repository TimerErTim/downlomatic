package eu.timerertim.downlomatic.console

/**
 * Provides helpful methods for CLI and terminal interaction. This special implementation
 * handles the server program.
 */
object ConsoleUtils : Console(
    ConsoleConfig(
        ServerArgument.values(),
        ServerArgumentGroup.values(),
        "server [--ignore-redundancy] [--port <port>]"
    )
)
