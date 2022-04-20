package eu.timerertim.downlomatic.core.scraping

import eu.timerertim.downlomatic.api.toEntry
import eu.timerertim.downlomatic.core.db.HostEntry
import eu.timerertim.downlomatic.core.db.VideoEntry
import eu.timerertim.downlomatic.core.host.Host
import eu.timerertim.downlomatic.core.video.Video
import eu.timerertim.downlomatic.util.db.MongoDB
import eu.timerertim.downlomatic.util.logging.Log
import kotlinx.coroutines.*
import org.litote.kmongo.div
import org.litote.kmongo.eq
import org.litote.kmongo.updateOne
import org.litote.kmongo.upsert
import org.reflections.Reflections
import org.reflections.ReflectionsException
import kotlin.system.measureTimeMillis

/**
 * Object responsible for scraping [Video]s from known [HostScraper]s. Hosts are known if they are
 * in the [eu.timerertim.downlomatic.hosts] package.
 */
object Scraper {
    @OptIn(DelicateCoroutinesApi::class)
    private val dispatcher = newSingleThreadContext("Web Scraper")
    private val scope = CoroutineScope(dispatcher)

    /**
     * Controls the removal and skipping of redundant elements and data.
     */
    var patchRedundancy = true

    /**
     * Starts to fetch the given [scrapers]. The fetching process continues until [stop] is invoked.
     */
    fun start(scrapers: List<HostScraper>) {
        scrapers.forEach {
            scope.launch {
                while (isActive) {
                    val duration = try {
                        measureTimeMillis {
                            it.fetch()
                        }
                    } catch (ex: Exception) {
                        Log.e("An error occurred while scraping host ${it.host.domain}", ex)
                        Long.MAX_VALUE
                    }
                    delay(604800000L - duration) // 604800000 is the amount of ms a week has
                }
            }
        }
    }

    /**
     * Stops the fetching process.
     */
    fun stop() {
        dispatcher.close()
    }
}

/**
 * Starts the fetching process and continues it indefinitely without blocking until [stopScraper] is invoked.
 */
fun startScraper() {
    // Find all hosts to fetch for
    val hostsKlass = try {
        Reflections("eu.timerertim.downlomatic.hosts").getSubTypesOf(HostScraper::class.java).map { it.kotlin }
    } catch (ex: ReflectionsException) {
        emptyList()
    }
    val hostScrapers = hostsKlass.mapNotNull {
        try {
            it.objectInstance ?: it.java.getDeclaredConstructor().newInstance()
        } catch (ex: ReflectiveOperationException) {
            null
        }
    }

    // Informs about found hosts to scrape
    Log.d(if (hostScrapers.isEmpty()) {
        "Found no host to scrape"
    } else {
        "Found following hosts to scrape: " + hostScrapers.joinToString { it.host.domain }
    })

    // Initialize hosts in db
    val hostCollection = MongoDB.hostCollection
    hostScrapers.map { it.host.toEntry() }.forEach {
        hostCollection.updateOne(it, upsert())
    }

    // Remove redundant host collections from db
    if (Scraper.patchRedundancy) {
        val removableHosts = hostCollection.find().toMutableList().apply {
            removeAll(hostScrapers.map { it.host.toEntry() })
        }
        if (removableHosts.isNotEmpty()) {
            Log.w(
                "Following hosts are redundant and will be removed from the database: " +
                        removableHosts.joinToString()
            )
            removableHosts.forEach {
                hostCollection.deleteMany(HostEntry::host / Host::domain eq it.host.domain)
                MongoDB.videoCollection.deleteMany(VideoEntry::video / Video::host / Host::domain eq it.host.domain)
            }
        }
    }

    Scraper.start(hostScrapers)
}

/**
 * Stops the fetching process. Is equivalent to [Scraper.stop].
 */
fun stopScraper() {
    Scraper.stop()
}
