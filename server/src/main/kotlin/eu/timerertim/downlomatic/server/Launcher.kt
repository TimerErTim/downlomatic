package eu.timerertim.downlomatic.server

import eu.timerertim.downlomatic.api.startKtor
import eu.timerertim.downlomatic.api.stopKtor
import eu.timerertim.downlomatic.console.ConsoleUtils
import eu.timerertim.downlomatic.console.ParsedArguments
import eu.timerertim.downlomatic.console.ServerArgument
import eu.timerertim.downlomatic.core.fetch.startScraper
import eu.timerertim.downlomatic.core.fetch.stopScraper
import eu.timerertim.downlomatic.util.ServerUtils
import eu.timerertim.downlomatic.util.Utils
import eu.timerertim.downlomatic.util.logging.Log
import java.util.*
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
            } catch (exception: Exception) {
                Log.f("A fatal error broke the execution", exception)
                exitProcess(Utils.GENERIC_EXIT_CODE)
            }
        } else {
            arguments.missingArgumentMessage?.let { ConsoleUtils.showErrorHelpMessage(it) }
        }
    }
}

private fun startServer() {
    startScraper()
    startKtor()
    Log.i("Server started")

    Runtime.getRuntime().addShutdownHook(Thread {
        stopServer()
        ServerUtils.cleanup()
    })

    // Checks for user input exiting the program
    val input = Scanner(System.`in`)
    while (true) {
        if (input.nextLine().trim().lowercase(Locale.getDefault()) == "exit") break
    }
}

private fun stopServer() {
    Log.i("Stopping Server...")
    stopKtor()
    stopScraper()
}
