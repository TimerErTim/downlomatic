package eu.timerertim.downlomatic.core.db

import eu.timerertim.downlomatic.core.downloader.Downloader
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class DownloaderEntry(
    @SerialName("_id")
    val id: Long,
    @Contextual
    val expireAt: Instant?,
    val downloader: Downloader<*>
)
