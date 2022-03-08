package eu.timerertim.downlomatic.api

import eu.timerertim.downlomatic.core.meta.VideoDetails
import eu.timerertim.downlomatic.core.parsing.Parser
import eu.timerertim.downlomatic.util.json.URLSerializer
import kotlinx.serialization.Serializable
import java.net.URL

@Serializable
data class VideoEntry(
    @Serializable(with = URLSerializer::class)
    val url: URL,
    val fetcher: Parser,
    val details: VideoDetails
) {
    val _id = details.idHash
}
