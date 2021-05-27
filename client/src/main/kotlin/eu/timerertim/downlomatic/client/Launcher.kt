package eu.timerertim.downlomatic.client

import eu.timerertim.downlomatic.console.ClientArgument
import eu.timerertim.downlomatic.console.ConsoleUtils
import eu.timerertim.downlomatic.console.ParsedArguments
import eu.timerertim.downlomatic.utils.ClientUtils
import eu.timerertim.downlomatic.utils.Utils
import eu.timerertim.downlomatic.utils.logging.Log
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    @Suppress("NAME_SHADOWING")
    val args = ConsoleUtils.parseArgs(*args)
    processArgs(args)
    exitProcess(0)
}

private fun processArgs(arguments: ParsedArguments) {
    if (arguments.hasArgument(ClientArgument.HELP)) {
        ConsoleUtils.printHelp()
    } else {
        if (arguments.hasRequiredArguments()) {
            ClientUtils.setup(arguments)

            // Start client, catch and handle exceptions
            try {
                startClient()
                ClientUtils.exit()
            } catch (exception: Exception) {
                Log.f("A fatal error broke the execution", exception)
                ClientUtils.exit(Utils.GENERIC_EXIT_CODE)
            }
        } else {
            arguments.missingArgumentMessage?.let { ConsoleUtils.showErrorHelpMessage(it) }
        }
    }
}

private fun startClient() {
    //TODO: Implement client main functionality
}