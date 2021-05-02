package eu.timerertim.downlomatic.console

import org.apache.commons.cli.Option
import org.apache.commons.cli.ParseException
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle

@TestInstance(Lifecycle.PER_CLASS)
class ConsoleTest {
    lateinit var console: Console
    val singleArgument = object : Argument {
        override val option = Option("a", "all", true, "test").apply {
            isRequired = true
        }
        override val isHidden = false
    }

    @BeforeAll
    fun `Init Console`() {
        val config = ConsoleConfig(
            arrayOf(
                singleArgument
            ),
            emptyArray()
        )

        console = Console(config)
    }

    @Test
    fun `Print help without error`() {
        val expected = "success"

        val result = try {
            console.printHelp()
            expected
        } catch (e: Exception) {
            "fail"
        }

        assertEquals(expected, result)
    }

    @Test
    fun `Missing argument state can be requested`() {
        val expected = false

        val parsedArguments = console.parseArgs()
        val result = parsedArguments.hasRequiredArguments()

        assertEquals(expected, result)
    }

    @Test
    fun `No missing arguments can be requested`() {
        val expected = true

        val parsedArguments = console.parseArgs("-a", "stuff")
        val result = parsedArguments.hasRequiredArguments()

        assertEquals(expected, result)
    }

    @Test
    fun `Unrecognized arguments should cause error`() {
        val expected = "fail"

        val result = try {
            console.parseArgs("---wtf", "dripplenipple")
            "success"
        } catch (e: ParseException) {
            expected
        }

        assertEquals(expected, result)
    }

    @Test
    fun `ParsedArguments size represents right amount`() {
        val expected = 1

        val parsedArguments = console.parseArgs("-a", "stuff")
        val result = parsedArguments.size

        assertEquals(expected, result)
    }

    @Test
    fun `hasArgument works`() {
        val expected = true

        val parsedArguments = console.parseArgs("-a", "testStuff")
        val result = parsedArguments.hasArgument(singleArgument) xor parsedArguments.hasArgument(object : Argument {
            override val option = Option("t", "stuff")
            override val isHidden = true
        })

        assertEquals(expected, result)
    }

    @Test
    fun `Getting value of parsed argument returns the right value`() {
        val value = "testValue"
        val expected = value

        val parsedArguments = console.parseArgs("-a", value)
        val result = if (parsedArguments.hasArgument(singleArgument)) {
            parsedArguments[singleArgument]
        } else {
            ""
        }

        assertEquals(expected, result)
    }

    @Test
    fun `Multiple values work for single value`() {
        val value = arrayOf("testValue")
        val expected = value

        val parsedArguments = console.parseArgs("-a", value[0])
        val result = parsedArguments.getValues(singleArgument)

        assertArrayEquals(expected, result)
    }
}
