package eu.timerertim.downlomatic.core.fetch.nodes

import com.mongodb.client.model.ReplaceOptions
import eu.timerertim.downlomatic.api.VideoEntry
import eu.timerertim.downlomatic.api.toEntry
import eu.timerertim.downlomatic.core.fetch.Fetcher
import eu.timerertim.downlomatic.core.fetch.Host
import eu.timerertim.downlomatic.core.fetch.PlainFetcher
import eu.timerertim.downlomatic.core.video.Video
import eu.timerertim.downlomatic.util.MongoDBConnection
import eu.timerertim.downlomatic.util.logging.Log
import org.litote.kmongo.getCollection
import org.litote.kmongo.replaceOneById
import java.net.URI
import java.net.URL

class VideoNode(
    parentNode: ParentNode,
    url: URL,
    private val fetcher: Fetcher = PlainFetcher,
    private val modify: suspend VideoNode.() -> Unit
) :
    Node(parentNode as Node), ChildNode {
    private val url = URL(
        URI(url.protocol, url.userInfo, url.host, url.port, url.path, url.query, url.ref)
            .toASCIIString().replace("%25", "%")
    )

    init {
        attachTo(parentNode)
    }

    override suspend fun fetch() {
        try {
            modify()
        } catch (ex: Exception) {
            Log.e("An error occurred while modifying video under URL \"$url\" of host ${host.domain}", ex)
            return
        }

        // Generate video object
        val details = videoDetailsBuilder.build()
        try {
            val video = Video(url, fetcher, details)

            // Insert into db or display on screen
            if (!hostConfig.testing) {
                val collection = MongoDBConnection.db.getCollection<VideoEntry>(host.domain)
                collection.replaceOneById(details.idHash, video.toEntry(), ReplaceOptions().apply {
                    upsert(true)
                })
            } else {
                Log.d(video.toString())
            }
        } catch (ex: Exception) {
            Log.e("A problem occurred while trying to fetch video from URL \"$url\"", ex)
        }

        // Register the video as inserted
        host.idVideos += details.idHash
    }

    private val Host.idVideos get() = with(this) { _idVideos }
}
