package eu.timerertim.downlomatic.console

import org.apache.commons.cli.*
import java.util.*

/**
 * Provides helpful methods for CLI and terminal interaction.
 */
open class Console(config: ConsoleConfig) {
    companion object {
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
                }
                return cmd
            }
        }
    }

    private val usage = config.usage
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
     * Prints a command line help message
     */
    fun printHelp() {
        val help = HelpFormatter()
        help.printHelp(
            (usage ?: "downlomatic") + "\n\n",
            header,
            shownArguments,
            "\n" + footer,
            usage == null
        )
    }

    @JvmSynthetic
    private fun ParsedArguments.Companion.build(line: CommandLine): ParsedArguments =
        with(ParsedArguments.Companion) { return _build(line) }
}