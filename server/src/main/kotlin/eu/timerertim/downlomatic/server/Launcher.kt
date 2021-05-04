package eu.timerertim.downlomatic.server

import eu.timerertim.downlomatic.console.ConsoleUtils
import eu.timerertim.downlomatic.console.ParsedArguments
import eu.timerertim.downlomatic.console.ServerArguments
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val args = ConsoleUtils.parseArgs(*args)
    processArgs(args)
}

fun processArgs(arguments: ParsedArguments) {
    if (arguments.hasArgument(ServerArguments.HELP)) {
        ConsoleUtils.printHelp()
        exitProcess(0)
    }
}