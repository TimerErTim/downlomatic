package eu.timerertim.downlomatic.console

import org.apache.commons.cli.ParseException
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class ConsoleUtilsTest {
    @Test
    fun `Print help without error`() {
        assertDoesNotThrow { ConsoleUtils.printHelp() }
    }

    @Test
    fun `Missing argument state can be requested`() {
        val expected = true//false

        val parsedArguments = ConsoleUtils.parseArgs()
        val result = parsedArguments.hasRequiredArguments()

        assertEquals(expected, result)
    }

    @Test
    fun `No missing arguments can be requested`() {
        val expected = true

        val parsedArguments = ConsoleUtils.parseArgs("-d", "hsad", "-nsfw", "-v")
        val result = parsedArguments.hasRequiredArguments()

        assertEquals(expected, result)
    }

    @Test
    fun `Unrecognized arguments should cause error`() {
        assertThrows<ParseException> { ConsoleUtils.parseArgs("---wtf", "dripplenipple") }
    }

    @Test
    fun `ParsedArguments size represents right amount`() {
        val expected = 3

        val parsedArguments = ConsoleUtils.parseArgs("-d", "hsad", "-x", "--no-gui")
        val result = parsedArguments.size

        assertEquals(expected, result)
    }

    @Test
    fun `hasArgument works`() {
        val expected = true

        val parsedArguments = ConsoleUtils.parseArgs("-x")
        val result =
            parsedArguments.hasArgument(ClientArgument.NSFW) xor parsedArguments.hasArgument(ClientArgument.DESTINATION)

        assertEquals(expected, result)
    }

    @Test
    fun `Getting value of parsed argument returns the right value`() {
        val value = "testValue"
        val expected = value

        val parsedArguments = ConsoleUtils.parseArgs("-d", value)
        val result = if (parsedArguments.hasArgument(ClientArgument.DESTINATION)) {
            parsedArguments[ClientArgument.DESTINATION]
        } else {
            ""
        }

        assertEquals(expected, result)
    }

    @Test
    fun `Multiple values work for single value`() {
        val value = arrayOf("testValue")
        val expected = value

        val parsedArguments = ConsoleUtils.parseArgs("-d", value[0])
        val result = parsedArguments.getValues(ClientArgument.DESTINATION)

        assertArrayEquals(expected, result)
    }

    @Test
    fun `Multiple values work for empty values`() {
        val expected = emptyArray<String>()

        val parsedArguments = ConsoleUtils.parseArgs("-x")
        val result = parsedArguments.getValues(ClientArgument.DESTINATION)
            .union(parsedArguments.getValues(ClientArgument.DESTINATION).toList()).toTypedArray()

        assertArrayEquals(expected, result)
    }

    @Test
    fun `Shown and hidden argument amount adds up to total amount`() {
        val parsedArguments = ConsoleUtils.parseArgs("--nsfw", "--no-gui", "--no-file-logging", "-v", "-d")

        val expected = parsedArguments.size
        val result = parsedArguments.sizeHidden + parsedArguments.sizeShown

        assertEquals(expected, result)
    }
}
