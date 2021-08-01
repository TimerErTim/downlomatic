package eu.timerertim.downlomatic.core.fetch

import eu.timerertim.downlomatic.core.fetch.nodes.RootNode
import eu.timerertim.downlomatic.core.fetch.nodes.VideoNode
import eu.timerertim.downlomatic.core.video.Video
import eu.timerertim.downlomatic.utils.MongoDBConnection
import eu.timerertim.downlomatic.utils.logging.Level
import eu.timerertim.downlomatic.utils.logging.Log
import kotlinx.coroutines.runBlocking
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
    private val idVideos = mutableListOf<Int>()

    /**
     * Executes the fetching process in a blocking way.
     */
    suspend fun fetch() {
        root.fetch()
        // Remove videos which haven't been fetched.
        if (Fetcher.patchRedundancy && !config.testing) {
            val collection = MongoDBConnection.db.getCollection<Video>(config.domain)
            val deletedCount = collection.deleteMany(Video::_id nin idVideos).deletedCount
            if (deletedCount > 0) {
                Log.w("$deletedCount videos were removed from the ${config.domain} MongoDB collection")
            }
        }
    }

    private suspend fun RootNode.fetch() = with(this) { _fetch() }

    val VideoNode._idVideos get() = idVideos

    /**
     * This class can be used to [test] a specific [host] instance.
     */
    class Tester(private val host: Host) {

        /**
         * Tests the [host] by executing the fetch method but not using the
         * [MongoDBConnection][eu.timerertim.downlomatic.utils.MongoDBConnection]. The resulting
         * [Video][eu.timerertim.downlomatic.core.video.Video] will be printed on the screen instead of being inserted
         * into the database.
         */
        fun test() {
            host.config.setTesting(true)
            val consoleVerbosity = Log.consoleVerbosity
            Log.consoleVerbosity = Level.DEBUG

            try {
                runBlocking { host.fetch() }
            } finally {
                Log.consoleVerbosity = consoleVerbosity
                host.config.setTesting(false)
            }
        }

        private fun HostConfig.setTesting(value: Boolean) = with(this) { _setTesting(value) }
    }
}
