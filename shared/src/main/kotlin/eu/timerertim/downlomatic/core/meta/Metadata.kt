package eu.timerertim.downlomatic.core.meta

import eu.timerertim.downlomatic.utils.Utils.generateSHA512Checksum
import java.io.InputStream
import java.time.LocalDateTime

/**
 * This class contains all metadata information about a video.
 *
 * It contains information such as size, type, date of last edit and so on.
 */
data class Metadata(
    val size: Long = 0,
    val fileType: String = "",
    val httpType: String? = null,
    val lastModified: LocalDateTime? = null
) : Comparable<Metadata> {
    /**
     * The SHA512 Checksum of the actual video file
     */
    var checksum = ""
        private set

    /**
     * Compares this and one [other] Metadata.
     * What really is compared is the currency:
     * - 0 means both are equal
     * - positive numbers mean this is more current than [other]
     * - negative numbers mean [other] is more current than this
     */
    override fun compareTo(other: Metadata): Int {
        // Check based on last time modified
        if (lastModified != null && other.lastModified != null) {
            return lastModified.compareTo(other.lastModified)
        }

        // Check based on file and http type
        if (httpType != null && other.httpType != null && httpType != other.httpType) {
            return -1
        } else if (fileType != other.fileType) {
            return -1
        }

        // Check based on size
        if (size != other.size || other.size < 0) {
            return -1
        }

        return 0
    }

    /**
     * Returns true if this is uptodate with the [new] Metadata and
     * false if it should be updated.
     */
    fun isUpToDate(new: Metadata) = compareTo(new) >= 0


    /**
     * Injects the SHA512 checksum calculated based on different inputs into
     * [target].
     */
    class ChecksumInjector(private val target: Metadata) {
        /**
         * Injects the SHA512 checksum calculated from [input] into the [target].
         */
        fun inject(input: InputStream) {
            target.checksum = input.generateSHA512Checksum()
        }

        /**
         * Injects the [other] SHA512 checksum into the [target].
         */
        fun inject(other: Metadata) {
            target.checksum = other.checksum
        }
    }
}