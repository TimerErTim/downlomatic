package eu.timerertim.downlomatic.core.db

import eu.timerertim.downlomatic.core.meta.VideoDetails
import eu.timerertim.downlomatic.core.parsing.Parser
import eu.timerertim.downlomatic.util.json.URLSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.net.URL

@Serializable
data class VideoEntry(
    @Serializable(with = URLSerializer::class)
    val url: URL,
    val host: HostEntry,
    val parser: Parser,
    val details: VideoDetails
) {
    val idHash by lazy {
        var result = 1L * (details.idHash)
        result = 63 * result + (host.hashCode())
        result
    }

    @SerialName("_id")
    val id = idHash
}
