package eu.timerertim.downlomatic.util

import eu.timerertim.downlomatic.util.Utils.floor
import eu.timerertim.downlomatic.util.Utils.generateSHA512Checksum
import eu.timerertim.downlomatic.util.Utils.pow
import eu.timerertim.downlomatic.util.Utils.toHumanReadableBytesBin
import eu.timerertim.downlomatic.util.Utils.toHumanReadableBytesSI
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import java.nio.charset.StandardCharsets


@TestInstance(Lifecycle.PER_CLASS)
class UtilsTest {
    @Test
    fun `Checksum returns right value`() {
        // Evaluated by: https://emn178.github.io/online-tools/sha512.html
        val expected =
            "ba3253876aed6bc22d4a6ff53d8406c6ad864195ed144ab5c87621b6c233b548baeae6956df346ec8c17f5ea10f35ee3cbc514797ed7ddd3145464e2a0bab413"

        val source = "123456"
        val stream = source.byteInputStream(StandardCharsets.UTF_8)

        assertEquals(expected, stream.generateSHA512Checksum())
    }

    @Test
    fun `Pow extension function of Long`() {
        assertEquals(1, 1000L pow 0)
        assertEquals(1, 0L pow 0)
        assertEquals(4, 2L pow 2)
        assertEquals(27, 3L pow 3)
        assertEquals(5, 5L pow 1)
    }

    @Test
    fun `Floor digit extension function of Double`() {
        assertEquals(1.2, 1.26.floor(1))
        assertEquals(5.0, 5.87123.floor(0))
        assertEquals(3.25, 3.25.floor(2))
        assertEquals(-1.3, (-1.22).floor(1))
    }

    @Test
    fun `Number to bytes`() {
        assertEquals("2.1MB", 2_120_000.toHumanReadableBytesSI())
        assertEquals("20B", 20.toHumanReadableBytesSI())
        assertEquals("30B", 30.toHumanReadableBytesBin())
        assertEquals("1.9KiB", 2000.toHumanReadableBytesBin())
    }
}
