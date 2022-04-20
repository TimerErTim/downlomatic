package eu.timerertim.downlomatic.core.scraping.nodes

import com.mongodb.client.model.FindOneAndReplaceOptions
import com.mongodb.client.model.ReturnDocument
import eu.timerertim.downlomatic.api.toEntry
import eu.timerertim.downlomatic.core.db.VideoEntry
import eu.timerertim.downlomatic.core.parsing.Parser
import eu.timerertim.downlomatic.core.parsing.PlainParser
import eu.timerertim.downlomatic.core.scraping.HostScraper
import eu.timerertim.downlomatic.core.video.Video
import eu.timerertim.downlomatic.util.db.MongoDB
import eu.timerertim.downlomatic.util.logging.Log
import org.litote.kmongo.deleteOneById
import org.litote.kmongo.eq
import java.net.URI
import java.net.URL

class VideoNode(
    parentNode: ParentNode,
    url: URL,
    private val parser: Parser = PlainParser,
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
            Log.e("An error occurred while modifying video under URL \"$url\" of host ${scraper.host.domain}", ex)
            return
        }

        // Generate video object
        val details = videoDetailsBuilder.build()
        val video = Video(url, scraper.host, details)
        try {
            // Insert into db or display on screen
            if (!hostConfig.testing) {
                insertIntoDB(video)
            } else {
                Log.d(video)
                Log.d(parser(video.url).descriptor)
            }
        } catch (ex: Exception) {
            Log.e("A problem occurred while trying to fetch video from URL \"$url\"", ex)
        }

        // Register the video as inserted
        scraper.idVideos += video.idHash
    }

    private fun insertIntoDB(video: Video) {
        val videoEntry = video.toEntry(parser)

        val oldVideo = MongoDB.videoCollection.findOneAndReplace(VideoEntry::id eq videoEntry.id, videoEntry,
            FindOneAndReplaceOptions().apply {
                upsert(true)
                returnDocument(ReturnDocument.BEFORE)
            })

        if (oldVideo != null && oldVideo != videoEntry) {
            val deletedCount = MongoDB.downloaderCollection.deleteOneById(oldVideo.id).deletedCount
            if (deletedCount > 0) {
                val id = oldVideo.id
                val domain = oldVideo.video.host.domain
                Log.i("Downloader $id of host $domain was removed because it is outdated")
            }
        }
    }

    private val HostScraper.idVideos get() = with(this) { _idVideos }
}
