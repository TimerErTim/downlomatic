package eu.timerertim.downlomatic.console

import org.apache.commons.cli.ParseException
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ConsoleUtilsTest {
    @Test
    fun `Print help without error`() {
        val expected = "success"

        val result = try {
            ConsoleUtils.printHelp()
            expected
        } catch (e: Exception) {
            "fail"
        }

        assertEquals(expected, result)
    }

    @Test
    fun `Missing argument state can be requested`() {
        val expected = false

        val parsedArguments = ConsoleUtils.parseArgs("-t", "10")
        val result = parsedArguments.hasRequiredArguments()

        assertEquals(expected, result)
    }

    @Test
    fun `No missing arguments can be requested`() {
        val expected = true

        val parsedArguments = ConsoleUtils.parseArgs("-d", "hsad", "-h", "ALL", "-a")
        val result = parsedArguments.hasRequiredArguments()

        assertEquals(expected, result)
    }

    @Test
    fun `Unrecognized arguments should cause error`() {
        val expected = "fail"

        val result = try {
            ConsoleUtils.parseArgs("---wtf", "dripplenipple")
            "success"
        } catch (e: ParseException) {
            expected
        }

        assertEquals(expected, result)
    }

    @Test
    fun `ParsedArguments size represents right amount`() {
        val expected = 3

        val parsedArguments = ConsoleUtils.parseArgs("-d", "hsad", "-h", "ALL", "-a")
        val result = parsedArguments.size

        assertEquals(expected, result)
    }

    @Test
    fun `hasArgument works`() {
        val expected = true

        val parsedArguments = ConsoleUtils.parseArgs("-a")
        val result =
            parsedArguments.hasArgument(ClientArguments.ALL) xor parsedArguments.hasArgument(ClientArguments.SERIES)

        assertEquals(expected, result)
    }

    @Test
    fun `Getting value of parsed argument returns the right value`() {
        val value = "testValue"
        val expected = value

        val parsedArguments = ConsoleUtils.parseArgs("-d", value)
        val result = if (parsedArguments.hasArgument(ClientArguments.DESTINATION_DIRECTORY)) {
            parsedArguments[ClientArguments.DESTINATION_DIRECTORY]
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
        val result = parsedArguments.getValues(ClientArguments.DESTINATION_DIRECTORY)

        assertArrayEquals(expected, result)
    }

    @Test
    fun `Multiple values work for empty values`() {
        val expected = emptyArray<String>()

        val parsedArguments = ConsoleUtils.parseArgs("-a")
        val result = parsedArguments.getValues(ClientArguments.DESTINATION_DIRECTORY)
            .union(parsedArguments.getValues(ClientArguments.ALL).toList()).toTypedArray()

        assertArrayEquals(expected, result)
    }
}
