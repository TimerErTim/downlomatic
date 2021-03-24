package eu.timerertim.downlomatic.core.format

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class EpisodeFormatTest {
    val seperator = File.separator

    @Test
    fun `Literal should be replaced`() {
        val expected = "Test string${seperator}format"
        val format = EpisodeFormatBuilder().build()

        val result = format.format("Test string//format")

        assertEquals(expected, result)
    }

    @Test
    fun `Literal negative identifier should be ignored`() {
        val expected = "Test! string! will be printed"
        val format = EpisodeFormatBuilder().build()

        val result = format.format("Test/!/ string/[/!/ will be printed/]")

        assertEquals(expected, result)
    }

    @Test
    fun `Literal in combination with other identifiers`() {
        val expected = "Titel Name der Episode${seperator}Nummer Trans ${seperator}T"
        val format = EpisodeFormatBuilder()
            .setEpisodeName("Name der Episode")
            .setEpisodeNumber("Nummer")
            .setTranslationType("Sub")
            .build()

        val result = format.format("/[Titel: /E///e/] Trans: //T/[ Serie: /S//s/]")

        assertEquals(expected, result)
    }

    @Test
    fun `Many identifiers in combination with each other`() {
        val expected =
            "Number is the number and Series Name is the series this block should be displayed with number " +
                    "Number display this name Episode Name"
        val format = EpisodeFormatBuilder()
            .setSeriesName("Series Name")
            .setEpisodeNumber("Number")
            .setEpisodeName("Episode Name")
            .build()

        val result = format.format(
            "/e is the number/[ and /S is the series/]/[ /E the name with language /L/]" +
                    "/[ this block should be displayed with number /e/!L/]/[ display this name /E/[ with type /T/]/]"
        )

        assertEquals(expected, result)
    }

    @Test
    fun `Replacement should work simultaneously not iteratively`() {
        val expected = "${seperator}${seperator}${seperator} Text ${seperator}Series Name"
        val format = EpisodeFormatBuilder()
            .setSeriesName("Series Name")
            .setEpisodeNumber("Number")
            .setEpisodeName("Episode Name")
            .build()

        val result = format.format(
            "/[/////[///]/] Text: /[/[///]/[/S/]/]"
        )

        assertEquals(expected, result)
    }

    @Test
    fun `Faulty brackets should not cause error`() {
        val format = EpisodeFormatBuilder()
            .build()

        val result = try {
            format.format("/[///]/]///[/// ] Text: /[/[///]/[/S/]")
            "No error"
        } catch (e: RuntimeException) {
            "Error"
        }

        assertEquals("No error", result)
    }

    @Test
    fun `Negative Identifiers in brackets`() {
        val expected = "Series Name and"
        val format = EpisodeFormatBuilder()
            .setSeriesName("Series Name")
            .setEpisodeNumber("Number")
            .setEpisodeName("Episode Name")
            .build()

        val result = format.format(
            "/[/S/[/!S no series/]/] and/[/!S /[/S should not be displayed/]/]"
        )

        assertEquals(expected, result)
    }
}