package eu.timerertim.downlomatic.server

import eu.timerertim.downlomatic.console.ConsoleUtils
import eu.timerertim.downlomatic.console.ParsedArguments
import eu.timerertim.downlomatic.console.ServerArgument
import eu.timerertim.downlomatic.utils.ServerUtils
import eu.timerertim.downlomatic.utils.Utils
import eu.timerertim.downlomatic.utils.logging.Log
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    @Suppress("NAME_SHADOWING")
    val args = ConsoleUtils.tryParseArgs(*args)
    processArgs(args)
    exitProcess(0)
}

private fun processArgs(arguments: ParsedArguments) {
    if (arguments.hasArgument(ServerArgument.HELP)) {
        ConsoleUtils.printHelp()
    } else {
        if (arguments.hasRequiredArguments()) {
            ServerUtils.setup(arguments)


            // Start server, catch and handle exceptions
            try {
                startServer()
                ServerUtils.exit()
            } catch (exception: Exception) {
                Log.f("A fatal error broke the execution", exception)
                ServerUtils.exit(Utils.GENERIC_EXIT_CODE)
            }
        } else {
            arguments.missingArgumentMessage?.let { ConsoleUtils.showErrorHelpMessage(it) }
        }
    }
}

private fun startServer() {
    //TODO: Implement server main functionality
}
