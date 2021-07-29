package eu.timerertim.downlomatic.core.fetch

import eu.timerertim.downlomatic.core.fetch.nodes.RootNode
import eu.timerertim.downlomatic.core.video.Video
import eu.timerertim.downlomatic.utils.MongoDBConnection
import eu.timerertim.downlomatic.utils.logging.Log
import org.litote.kmongo.getCollection
import org.litote.kmongo.nin

/**
 * This is the superclass of all registered Hosts the server provides. It only needs two things:
 * - The [config] of the website which contains critical information
 * - A function which describes how to [fetch] videos from the host
 * In order for host implementations to be recognized, they need bo be placed in the package [eu.timerertim.downlomatic.pages].
 */
abstract class Host(
    val config: HostConfig,
    fetch: suspend RootNode.() -> Unit
) {
    private val root by lazy { RootNode(this, fetch) }
    val idVideos = mutableListOf<Int>()

    /**
     * Executes the fetching process in a blocking way.
     */
    suspend fun fetch() {
        root.fetch()
        // Remove videos which haven't been fetched.
        if (Fetcher.patchRedundancy) {
            val collection = MongoDBConnection.db.getCollection<Video>(config.domain)
            val deletedCount = collection.deleteMany(Video::_id nin idVideos).deletedCount
            if (deletedCount > 0) {
                Log.w("$deletedCount videos were removed from the ${config.domain} MongoDB collection")
            }
        }
    }

    suspend fun RootNode.fetch() = with(this) { _fetch() }
}
