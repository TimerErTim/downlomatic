package eu.timerertim.downlomatic.util

import eu.timerertim.downlomatic.util.logging.Log
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest
import kotlin.math.floor
import kotlin.system.exitProcess

/**
 * Provides some neutral helper functions and helps in code reuse.
 * Is also used as config object.
 */
object Utils {
    const val GENERIC_EXIT_CODE = 1
    const val CONNECTION_EXIT_CODE = 2
    const val ARGUMENT_EXIT_CODE = 3

    const val KTOR_PORT = 8081

    /**
     * Initializes stuff which is needed for
     * successful execution.
     */
    @JvmStatic
    fun setup() {
        // Empty
    }

    /**
     * Exits the program with the given [errorCode]. Additionally, it closes and cleans everything not needed anymore by
     * invoking [cleanup].
     */
    @JvmStatic
    @JvmOverloads
    fun exit(errorCode: Int = 0): Nothing {
        // Cleans up before exiting
        cleanup()

        // Exit with the given errorCode
        exitProcess(errorCode)
    }

    /**
     * Cleans and closes system resources and prepares for program exit. Is equivalent to [exit] but without the actual
     * program shutdown.
     */
    @JvmStatic
    fun cleanup() {
        // Close Logging object
        Log.close()
    }

    /**
     * Formats [this] number to a human readable amount of bytes. Units is MB-like.
     */
    @JvmStatic
    fun Number.toHumanReadableBytesPowerOfTen(): String {
        val bytes = this.toLong()

        return when {
            bytes == Long.MIN_VALUE || bytes < 0 -> "N/A"
            bytes < 1000L -> "${bytes}B"
            bytes <= 1000L pow 2 -> "%.1fKB".format((bytes.toDouble() / 1000L).floor(1))
            bytes <= 1000L pow 3 -> "%.1fMB".format((bytes.toDouble() / (1000L pow 2)).floor(1))
            bytes <= 1000L pow 4 -> "%.1fGB".format((bytes.toDouble() / (1000L pow 3)).floor(1))
            bytes <= 1000L pow 5 -> "%.1fTB".format((bytes.toDouble() / (1000L pow 4)).floor(1))
            bytes <= 1000L pow 6 -> "%.1fPB".format((bytes.toDouble() / (1000L pow 5)).floor(1))
            else -> "%.1fEB".format((bytes.toDouble() / (1000L pow 6)).floor(1))
        }
    }

    /**
     * Formats [this] number to a human readable amount of bytes. Units is MiB-like.
     */
    @JvmStatic
    fun Number.toHumanReadableBytesPowerOfTwo(): String {
        val bytes = this.toLong()

        return when {
            bytes == Long.MIN_VALUE || bytes < 0 -> "N/A"
            bytes < 1024L -> "${bytes}B"
            bytes <= 0xfffccccccccccccL shr 40 -> "%.1fKiB".format((bytes.toDouble() / (0x1 shl 10)).floor(1))
            bytes <= 0xfffccccccccccccL shr 30 -> "%.1fMiB".format((bytes.toDouble() / (0x1 shl 20)).floor(1))
            bytes <= 0xfffccccccccccccL shr 20 -> "%.1fGiB".format((bytes.toDouble() / (0x1 shl 30)).floor(1))
            bytes <= 0xfffccccccccccccL shr 10 -> "%.1fTiB".format((bytes.toDouble() / (0x1 shl 40)).floor(1))
            bytes <= 0xfffccccccccccccL -> "%.1fPiB".format(((bytes shr 10).toDouble() / (0x1 shl 40)).floor(1))
            else -> "%.1fEiB".format(((bytes shr 20).toDouble() / (0x1 shl 40)).floor(1))
        }
    }

    /**
     * Multiplies [this] Long [exponent] times with itself.
     */
    @JvmStatic
    infix fun Long.pow(exponent: Int): Long {
        var value = 1L
        repeat(exponent) {
            value *= this
        }
        return value
    }

    /**
     * Calculates the floor of [this] to the given number of [digit].
     */
    @JvmStatic
    fun Double.floor(digit: Int) = floor(this * (10L pow digit)) / (10L pow digit)

    /**
     * Calculates the SHA-512 checksum for [this] InputStream.
     *
     * Can throw an [IOException] when errors occur because of the InputStream.
     */
    @JvmStatic
    @Throws(IOException::class)
    fun InputStream.generateSHA512Checksum() =
        runBlocking { // Source: https://medium.com/codelit/generating-sha-512-checksum-of-a-file-in-android-42ae135b0a52
            // Generate the bytes
            val checksumBuffer = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            while (true) {
                val readNum = read(buffer)
                if (readNum == -1) break
                checksumBuffer.write(buffer, 0, readNum)
                yield()
            }

        // Generate the checksum
        val digest = MessageDigest.getInstance("SHA-512")
        val hash = digest.digest(checksumBuffer.toByteArray())

        // Create Hex String
        val hexString = StringBuilder()
        for (byte in hash) {
            var h: String = Integer.toHexString(0xFF and byte.toInt())
            while (h.length < 2)
                h = "0$h"
            hexString.append(h)
            yield()
        }
            hexString.toString()
    }
}
