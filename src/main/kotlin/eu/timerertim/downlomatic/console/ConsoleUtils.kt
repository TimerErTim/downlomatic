package eu.timerertim.downlomatic.console

import eu.timerertim.downlomatic.core.format.EpisodeFormat
import org.apache.commons.cli.*
import java.util.*

/**
 * Provides helpful methods for CLI and terminal interaction.
 */
object ConsoleUtils {
    private const val USAGE = "\"downlomatic -d <directory> -h <host> -a | -b <url> | -s <url> " +
            "[-f <formatting>] [--subdir-format <formatting>] [-t <amount>]\""
    private const val HEADER = ""
    private const val FOOTER =
        "Formatting follows the rules described under the following URL: ${EpisodeFormat.WIKI_URL}"

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

    private val allArguments = run {
        val options = Options()
        val arguments = Arguments.values().toList()
        val groups = ArgumentGroups.values().toList()
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

        Arguments.values().filter { !it.isHidden }
            .forEach { options.addOption(it.option) }

        options
    }

    /**
     * Returns [ConsoleParsedArguments] based on parsed [args].
     *
     * May throw [ParseException] if [args] violates the usage of downlomatic via CLI.
     */
    @JvmStatic
    @Throws(ParseException::class)
    fun parseArgs(vararg args: String): ConsoleParsedArguments {
        return ConsoleParsedArguments.build(parser.parse(allArguments, args))
    }

    /**
     * Prints a command line help message
     */
    @JvmStatic
    fun printHelp() {
        val help = HelpFormatter()
        help.printHelp(
            USAGE + "\n\n",
            HEADER,
            shownArguments,
            "\n" + FOOTER
        )
    }

    @JvmSynthetic
    private fun ConsoleParsedArguments.Companion.build(line: CommandLine): ConsoleParsedArguments =
        with(ConsoleParsedArguments.Companion) { return _build(line) }
}