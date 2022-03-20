package eu.timerertim.downlomatic.core.scraping

import eu.timerertim.downlomatic.api.toEntry
import eu.timerertim.downlomatic.core.db.DownloaderEntry
import eu.timerertim.downlomatic.core.db.VideoEntry
import eu.timerertim.downlomatic.core.host.Host
import eu.timerertim.downlomatic.core.scraping.nodes.RootNode
import eu.timerertim.downlomatic.core.scraping.nodes.VideoNode
import eu.timerertim.downlomatic.util.db.MongoDB
import eu.timerertim.downlomatic.util.logging.Level
import eu.timerertim.downlomatic.util.logging.Log
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.`in`
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.nin

/**
 * This is the superclass of all registered Hosts the server provides. It only needs three things:
 * - The [host] of the scraper. The scraper will be registered with this host's domain both internally and publicly.
 * - The [config] of the website which contains the technical configuration
 * - A function which describes how to [fetch] videos from the host
 * In order for scraper implementations to be recognized, they need bo be placed in the package [eu.timerertim.downlomatic.hosts].
 */
abstract class HostScraper(
    val host: Host,
    private val config: HostConfig,
    fetch: suspend RootNode.() -> Unit
) {
    constructor(domain: String, isNSWF: Boolean = false, config: HostConfig, fetch: suspend RootNode.() -> Unit) : this(
        Host(domain, isNSWF), config, fetch
    )

    private val root by lazy { RootNode(this, config, fetch) }
    private val idVideos = mutableListOf<Long>()

    /**
     * Executes the fetching process in a blocking way.
     */
    suspend fun fetch() {
        root.fetch()
        // Remove videos which haven't been fetched.
        if (Scraper.patchRedundancy && !config.testing) {
            val removableVideos = MongoDB.videoCollection.find(
                and(
                    VideoEntry::id nin idVideos,
                    VideoEntry::host eq host.toEntry()
                )
            ).toList()
            if (removableVideos.isNotEmpty()) {
                Log.w("${removableVideos.size} videos will be removed from ${host.domain}")
                val deletedCount = MongoDB.videoCollection.deleteMany(
                    VideoEntry::id `in` removableVideos.map { it.id }
                ).deletedCount
                val deletedAmountMessage = "$deletedCount videos were removed from ${host.domain}"
                if (deletedCount != removableVideos.size.toLong()) {
                    Log.w("$deletedAmountMessage, but ${removableVideos.size} should have been deleted")
                } else {
                    Log.i(deletedAmountMessage)
                }
                MongoDB.downloaderCollection.deleteMany(
                    DownloaderEntry::id `in` removableVideos.map { it.id }
                )
            }
        }
        idVideos.clear()
    }

    private suspend fun RootNode.fetch() = with(this) { _fetch() }

    val VideoNode._idVideos get() = idVideos

    /**
     * This class can be used to [test] a specific [host] instance.
     */
    class Tester(private val host: HostScraper) {

        /**
         * Tests the [host] by executing the fetch method but not using the
         * [MongoDBConnection][eu.timerertim.downlomatic.util.db.MongoDB]. The resulting
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
