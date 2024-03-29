package eu.timerertim.downlomatic.console

import eu.timerertim.downlomatic.util.Utils
import org.apache.commons.cli.*
import java.util.*
import kotlin.system.exitProcess

/**
 * Provides helpful methods for CLI and terminal interaction.
 */
open class Console(config: ConsoleConfig) {
    // Create a parser which doesn't fail on missing options
    private val parser = object : DefaultParser() {
        override fun parse(
            options: Options?,
            arguments: Array<out String>?,
            properties: Properties?,
            stopAtNonOption: Boolean
        ): CommandLine {
            try {
                super.parse(options, arguments, properties, stopAtNonOption)
            } catch (ex: MissingOptionException) {
                cmd.argList.add(0, "missing-required: ${ex.message}")
            } catch (ex: MissingArgumentException) {
                cmd.argList.add(0, "missing-parameter: ${ex.message}")
            }
            return cmd
        }
    }

    private val usage = config.usage
    private val name = config.executableName
    private val header = config.header
    private val footer = config.footer

    private val allArguments = run {
        val options = Options()
        val arguments = config.arguments.toList()
        val groups = config.argumentGroups.toList()
        val argumentsInGroups = mutableListOf<Option>()
        groups.forEach { argumentsInGroups.addAll(it.optionGroup.options) }
        val argumentsOutsideGroups = arguments.map { it.option }.toMutableList()
        argumentsOutsideGroups.removeAll(argumentsInGroups)

        argumentsOutsideGroups.forEach { options.addOption(it) }
        groups.forEach { options.addOptionGroup(it.optionGroup) }

        options
    }
    private val shownArguments = run {
        val options = Options()

        config.arguments.filter { !it.isHidden }
            .forEach { options.addOption(it.option) }

        options
    }

    /**
     * Returns [ParsedArguments] based on parsed [args].
     *
     * May throw [ParseException] if [args] violates the usage of downlomatic via CLI.
     */
    @Throws(ParseException::class)
    fun parseArgs(vararg args: String): ParsedArguments {
        return ParsedArguments.build(parser.parse(allArguments, args))
    }

    /**
     * Returns [ParsedArguments] based on parsed [args] or exits the program by showing a help message
     * in case of unrecognized given [args].
     */
    fun tryParseArgs(vararg args: String): ParsedArguments {
        try {
            return parseArgs(*args)
        } catch (ex: UnrecognizedOptionException) {
            showErrorHelpMessage(ex.localizedMessage)
        }
    }

    /**
     * Prints a command line help message
     */
    fun printHelp() {
        val help = HelpFormatter()
        help.printHelp(
            (usage ?: name ?: "Downlomatic"),
            "\n\n" + header,
            shownArguments,
            "\n" + footer,
            usage == null
        )
    }

    /**
     * Shows the specified [errorMessage] before executing [printHelp] and exiting with the specified [errorCode]
     * (defaults to 1)
     */
    @JvmOverloads
    fun showErrorHelpMessage(errorMessage: String, errorCode: Int = Utils.ARGUMENT_EXIT_CODE): Nothing {
        println(
            """
            $errorMessage
            
            """.trimIndent()
        )
        printHelp()
        exitProcess(errorCode)
    }

    @JvmSynthetic
    private fun ParsedArguments.Companion.build(line: CommandLine): ParsedArguments =
        with(ParsedArguments.Companion) { return _build(line, shownArguments) }
}
