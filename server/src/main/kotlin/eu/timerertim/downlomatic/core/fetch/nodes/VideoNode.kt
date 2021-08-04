package eu.timerertim.downlomatic.core.fetch.nodes

import com.google.common.io.Files
import com.mongodb.client.model.ReplaceOptions
import eu.timerertim.downlomatic.core.fetch.Fetcher
import eu.timerertim.downlomatic.core.fetch.Host
import eu.timerertim.downlomatic.core.meta.Metadata
import eu.timerertim.downlomatic.core.meta.VideoDetails
import eu.timerertim.downlomatic.core.video.Video
import eu.timerertim.downlomatic.utils.MongoDBConnection
import eu.timerertim.downlomatic.utils.logging.Log
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection
import org.litote.kmongo.replaceOneById
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.time.Instant
import java.time.ZoneId

class VideoNode(parentNode: ParentNode, url: URL, private val modify: suspend VideoNode.() -> Unit) :
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
            val metadata = generateMetadata(details)
            val video = Video(url, details, metadata)

            // Insert into db or display on screen
            if (!hostConfig.testing) {
                val collection = MongoDBConnection.db.getCollection<Video>(host.domain)
                collection.replaceOneById(details.idHash, video, ReplaceOptions().apply {
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

    private fun generateMetadata(details: VideoDetails): Metadata {
        val connection = url.openConnection()
        connection.connectTimeout = 120_000
        return if (connection is HttpURLConnection) {
            // Get Metadata fields
            val size = connection.contentLengthLong
            val fileType = Files.getFileExtension(connection.url.path).ifBlank { hostConfig.defaultFileType }
            val httpType = connection.contentType
            val lastModifiedMillis = connection.lastModified
            val lastModified = if (lastModifiedMillis == 0L) null else {
                Instant.ofEpochMilli(lastModifiedMillis).atZone(ZoneId.of("UTC")).toLocalDateTime()
            }

            // Create new metadata
            val metadata = Metadata(size, fileType, httpType, lastModified)

            // Compare it to old metadata if there is one
            val oldVideo = if (!hostConfig.testing) {
                MongoDBConnection.db.getCollection<Video>(host.domain).findOneById(details.idHash)
            } else {
                null
            }
            if (oldVideo != null && oldVideo.metadata.isUpToDate(metadata) && Fetcher.patchRedundancy) {
                Metadata.ChecksumInjector(metadata).inject(oldVideo.metadata)
            } else {
                Metadata.ChecksumInjector(metadata).inject(connection.inputStream)
            }

            connection.disconnect()

            metadata
        } else {
            throw IllegalArgumentException("URL must use the http protocol")
        }
    }

    private val Host.idVideos get() = with(this) { _idVideos }
}