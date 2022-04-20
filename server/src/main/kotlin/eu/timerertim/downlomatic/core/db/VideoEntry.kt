package eu.timerertim.downlomatic.core.db

import eu.timerertim.downlomatic.core.parsing.Parser
import eu.timerertim.downlomatic.core.video.Video
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoEntry(
    val parser: Parser,
    val video: Video
) {
    @SerialName("_id")
    val id = video.idHash
}
