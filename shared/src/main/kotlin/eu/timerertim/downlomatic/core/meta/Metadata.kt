package eu.timerertim.downlomatic.core.meta

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
)