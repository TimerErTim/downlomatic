package eu.timerertim.downlomatic.server

import eu.timerertim.downlomatic.console.ConsoleUtils
import eu.timerertim.downlomatic.console.ParsedArguments
import eu.timerertim.downlomatic.console.ServerArguments
import eu.timerertim.downlomatic.utils.ServerUtils
import eu.timerertim.downlomatic.utils.logging.Level
import eu.timerertim.downlomatic.utils.logging.Log
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    @Suppress("NAME_SHADOWING")
    val args = ConsoleUtils.parseArgs(*args)
    processArgs(args)
    exitProcess(0)
}

private fun processArgs(arguments: ParsedArguments) {
    if (arguments.hasArgument(ServerArguments.HELP)) {
        ConsoleUtils.printHelp()
    } else {
        if (arguments.hasRequiredArguments()) {
            ServerUtils.setup()

            // Further config
            if (arguments.hasArgument(ServerArguments.VERBOSE)) {
                Log.consoleVerbosity = Level.ALL
            }

            // Start server, catch and handle exceptions
            try {
                startServer()
                ServerUtils.exit(0)
            } catch (exception: Exception) {
                Log.f("A fatal error broke the execution", exception)
                ServerUtils.exit(3)
            }
        } else {
            arguments.missingArgumentMessage?.let { ConsoleUtils.showErrorHelpMessage(it) }
        }
    }
}

private fun startServer() {
    //TODO: Implement server main functionality
}
