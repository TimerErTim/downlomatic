package eu.timerertim.downlomatic.core.video

import eu.timerertim.downlomatic.core.meta.Metadata
import eu.timerertim.downlomatic.core.meta.VideoDetails
import eu.timerertim.downlomatic.util.json.URLSerializer
import kotlinx.serialization.Serializable
import java.net.URL

/**
 * This represents a single, downloadable video saved in the application. It contains all information needed
 * to identify a video, process and download it.
 */
@Serializable
data class Video(
    @Serializable(with = URLSerializer::class) val url: URL,
    val details: VideoDetails,
    val metadata: Metadata
) {
    val _id = details.idHash


    override fun toString(): String {
        return "Video(id=$_id, url=$url, details=$details, metadata=$metadata)"
    }
}
