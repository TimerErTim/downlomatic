package eu.timerertim.downlomatic.api

import eu.timerertim.downlomatic.core.fetch.Fetcher
import eu.timerertim.downlomatic.core.meta.VideoDetails
import eu.timerertim.downlomatic.util.json.URLSerializer
import kotlinx.serialization.Serializable
import java.net.URL

@Serializable
data class VideoEntry(
    @Serializable(with = URLSerializer::class)
    val url: URL,
    val fetcher: Fetcher,
    val details: VideoDetails
) {
    val _id = details.idHash
}
