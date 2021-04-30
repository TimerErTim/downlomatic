package eu.timerertim.downlomatic.console

import org.apache.commons.cli.CommandLine

/**
 * Holds parsed [Arguments] to quickly check for console parameters.
 */
class ConsoleParsedArguments private constructor(private val commandLine: CommandLine) {
    val missingArgumentMessage = commandLine.argList.getOrNull(0).takeIf { it.startsWith("missing-required: ") }
        .removePrefix("missing-required: ")
    val size = commandLine.options.size

    /**
     * Checks for all required [Arguments].
     *
     * Returns false if one or more required Arguments are missing.
     */
    fun hasRequiredArguments() = missingArgumentMessage == null

    /**
     * Returns true if the [arg] is present.
     */
    fun hasArgument(arg: Arguments) = commandLine.options.find { it == arg.option } != null

    /**
     * Returns the value for [arg] or null if it has none.
     */
    operator fun get(arg: Arguments) = commandLine.options.find { it == arg.option }.value

    /**
     * Returns all set values for [arg] or an empty array if it has no values.
     */
    fun getValues(arg: Arguments) = commandLine.options.find { it == arg.option }.values ?: emptyArray()

    companion object {
        @JvmSynthetic
        fun ConsoleUtils._build(line: CommandLine) = ConsoleParsedArguments(line)
    }
}