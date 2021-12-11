package eu.timerertim.downlomatic.client

import eu.timerertim.downlomatic.console.ClientArgument
import eu.timerertim.downlomatic.console.ConsoleUtils
import eu.timerertim.downlomatic.console.ParsedArguments
import eu.timerertim.downlomatic.graphics.GUI
import eu.timerertim.downlomatic.util.ClientUtils
import eu.timerertim.downlomatic.util.Utils
import eu.timerertim.downlomatic.util.logging.Log
import java.awt.HeadlessException
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    @Suppress("NAME_SHADOWING")
    val args = ConsoleUtils.tryParseArgs(*args)
    processArgs(args)
    exitProcess(0)
}

private fun processArgs(arguments: ParsedArguments) {
    if (arguments.hasArgument(ClientArgument.HELP)) {
        ConsoleUtils.printHelp()
    } else {
        // Decide starting method or exit if no viable one was found
        val start = if (arguments.hasArgument(ClientArgument.NO_GUI) && arguments.hasRequiredArguments()) {
            ::startClient
        } else if (!arguments.hasArgument(ClientArgument.NO_GUI)) {
            {
                try {
                    GUI.start()
                } catch (ex: HeadlessException) {
                    if (arguments.hasRequiredArguments()) {
                        startClient()
                    } else {
                        arguments.missingArgumentMessage!!.let { ConsoleUtils.showErrorHelpMessage(it) }
                    }
                }
            }
        } else {
            arguments.missingArgumentMessage!!.let { ConsoleUtils.showErrorHelpMessage(it) }
        }

        // Setup program for execution
        ClientUtils.setup(arguments)

        // Start client, catch and handle exceptions
        try {
            start()
            ClientUtils.exit()
        } catch (exception: Exception) {
            Log.f("A fatal error broke the execution", exception)
            ClientUtils.exit(Utils.GENERIC_EXIT_CODE)
        }
    }
}

private fun startClient() {
    //TODO: Implement client main functionality
}