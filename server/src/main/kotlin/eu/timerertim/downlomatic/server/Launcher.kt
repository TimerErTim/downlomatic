package eu.timerertim.downlomatic.server

import eu.timerertim.downlomatic.console.ConsoleUtils
import eu.timerertim.downlomatic.console.ParsedArguments

fun main(args: Array<String>) {
    val args = ConsoleUtils.parseArgs(*args)
    processArgs(args)
}

fun processArgs(arguments: ParsedArguments) {

}