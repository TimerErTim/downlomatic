package eu.timerertim.downlomatic.core.format

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EpisodeFormatTest {
    @Test
    fun `Literal should be replaced`() {
        val expected = "Test string/format"
        val format = EpisodeFormatBuilder().build()

        val result = format.format("Test string//format")

        assertEquals(expected, result)
    }

    @Test
    fun `Literal negative identifier should be ignored`() {
        val expected = "Test/!/ string/!/ will be printed"
        val format = EpisodeFormatBuilder().build()

        val result = format.format("Test/!/ string/[/!/ will be printed/]")

        assertEquals(expected, result)
    }

    @Test
    fun `Literal in combination with other identifiers`() {
        val expected = "Titel Name der Episode/Nummer Trans /T"
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
}