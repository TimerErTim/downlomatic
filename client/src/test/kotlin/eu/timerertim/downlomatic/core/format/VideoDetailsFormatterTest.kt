package eu.timerertim.downlomatic.core.format

import eu.timerertim.downlomatic.core.meta.Language
import eu.timerertim.downlomatic.core.meta.Translation
import eu.timerertim.downlomatic.core.meta.VideoDetails
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.File

@TestInstance(Lifecycle.PER_CLASS)
class VideoDetailsFormatterTest {
    val seperator = File.separator
    lateinit var videoDetails: VideoDetails

    @BeforeAll
    fun `Prepare VideoDetails`() {
        videoDetails = VideoDetails(
            title = "Titel des Videos",
            season = 4,
            spokenLanguage = Language.ENGLISH
        )
    }

    @Test
    fun `Literal should be replaced`() {
        val expected = "Test string${seperator}format"

        val result = VideoDetailsFormatter("Test string//format").format(videoDetails)

        assertEquals(expected, result)
    }

    @Test
    fun `Literal negative identifier should be ignored`() {
        val expected = "Test! string! will be printed"

        val result = VideoDetailsFormatter("Test/!/ string/[/!/ will be printed/]").format(videoDetails)

        assertEquals(expected, result)
    }

    @Test
    fun `Literal in combination with other identifiers`() {
        val expected = "Titel Name der Episode${seperator}5 Trans ${seperator}T"
        val videoDetails = VideoDetails(
            title = "Name der Episode",
            episode = 5,
            translation = Translation.SUB
        )

        val result = VideoDetailsFormatter("/[Titel: /N///e/] Trans: //T/[ Serie: /S//s/]").format(videoDetails)

        assertEquals(expected, result)
    }

    @Test
    fun `Many identifiers in combination with each other`() {
        val expected =
            "4 is the number and Series Name is the series this block should be displayed with number " +
                    "4 display this name Episode Name"
        val videoDetails = VideoDetails(
            series = "Series Name",
            episode = 4,
            title = "Episode Name"
        )

        val result = VideoDetailsFormatter(
            "/e is the number/[ and /S is the series/]/[ /N the name with language /V/]" +
                    "/[ this block should be displayed with number /e/!V/]/[ display this name /N/[ with type /y/]/]"
        ).format(videoDetails)

        assertEquals(expected, result)
    }

    @Test
    fun `Replacement should work simultaneously not iteratively`() {
        val expected = "${seperator}${seperator}${seperator} Text ${seperator}Series Name"
        val videoDetails = VideoDetails(
            series = "Series Name"
        )

        val result = VideoDetailsFormatter(
            "/[/////[///]/] Text: /[/[///]/[/S/]/]"
        ).format(videoDetails)

        assertEquals(expected, result)
    }

    @Test
    fun `Faulty brackets should not cause error`() {
        val format = VideoDetailsFormatter("/[///]/]///[/// ] Text: /[/[///]/[/S/]")

        assertDoesNotThrow { format.format(VideoDetails()) }
    }

    @Test
    fun `Negative Identifiers in brackets`() {
        val expected = "Series Name and"
        val videoDetails = VideoDetails(
            series = "Series Name",
            episode = 4,
            title = "Episode Name"
        )

        val result = VideoDetailsFormatter(
            "/[/S/[/!S no series/]/] and/[/!S /[/S should not be displayed/]/]"
        ).format(videoDetails)

        assertEquals(expected, result)
    }

    @Test
    fun `Illegals in identifier content should be removed`() {
        val expected = "SeriesName and Number"
        val videoDetails = VideoDetails(
            series = "Series/Name",
            title = "Number<"
        )

        val result = VideoDetailsFormatter(
            "/[/S/[/!S no series/]/] and /N/[/!S /[/S should not be displayed/]/]"
        ).format(videoDetails)

        assertEquals(expected, result)
    }

    @Test
    fun `Negative top level identifiers should be removed`() {
        val expected = "Test string"
        val videoDetails = VideoDetails(
            title = "Name"
        )

        val result = VideoDetailsFormatter("Test/!N string/[/!/!N will be printed/]").format(videoDetails)

        assertEquals(expected, result)
    }

    @Test
    fun `Identifier in segment should only result in empty String when that identifier would be replaced`() {
        val expected = "Test $seperator!N ${seperator}String"
        val videoDetails = VideoDetails(
            title = "Name"
        )

        val result = VideoDetailsFormatter("Test /[//!N /]/[//String/]").format(videoDetails)

        assertEquals(expected, result)
    }

    @Test
    fun `Positive top level identifiers should be removed`() {
        val expected = "Tested string n stuff"
        val videoDetails = VideoDetails(
            title = "Yeah",
            series = "Series"
        )

        val result = VideoDetailsFormatter("Tested/?N string /[/!N will/]n stuff").format(videoDetails)

        assertEquals(expected, result)
    }

    @Test
    fun `Literal positive identifier should be ignored`() {
        val expected = "Test string will be printed"

        val result = VideoDetailsFormatter("Test/?/ string/[/?/ will be printed/]").format(videoDetails)

        assertEquals(expected, result)
    }

    @Test
    fun `Positive identifiers in brackets`() {
        val expected = "Series Name and Episode 4"
        val videoDetails = VideoDetails(
            series = "Series Name",
            episode = 4,
            title = "Episode Name"
        )

        val result = VideoDetailsFormatter(
            "/[/?N/S/[/!S/?N no series/]/] and/[/?S Episode /e/[/?y jap /]/]"
        ).format(videoDetails)

        assertEquals(expected, result)
    }
}
