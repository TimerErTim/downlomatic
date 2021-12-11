package eu.timerertim.downlomatic.core.fetch

import eu.timerertim.downlomatic.core.video.Video
import eu.timerertim.downlomatic.util.MongoDBConnection
import eu.timerertim.downlomatic.util.logging.Log
import kotlinx.coroutines.*
import org.reflections.Reflections
import org.reflections.ReflectionsException
import kotlin.system.measureTimeMillis

/**
 * Object responsible for fetching [Video]s from known [Host]s. Hosts are known if they are
 * in the [eu.timerertim.downlomatic.hosts] package.
 */
object Fetcher {
    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * Controls the removal and skipping of redundant elements and data.
     */
    var patchRedundancy = true

    /**
     * Starts to fetch the given [hosts]. The fetching process continues until [stop] is invoked.
     */
    fun start(hosts: List<Host>) {
        hosts.forEach {
            scope.launch {
                while (isActive) {
                    val duration = try {
                        measureTimeMillis {
                            it.fetch()
                        }
                    } catch (ex: Exception) {
                        Log.e("An error occurred while fetching host ${it.domain}", ex)
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
        scope.cancel()
    }
}

/**
 * Starts the fetching process and continues it indefinitely without blocking until [stopFetcher] is invoked.
 */
fun startFetcher() {
    // Find all hosts to fetch for
    val hostsKlass = try {
        Reflections("eu.timerertim.downlomatic.pages").getSubTypesOf(Host::class.java).map { it.kotlin }
    } catch (ex: ReflectionsException) {
        emptyList()
    }
    val hosts = hostsKlass.mapNotNull {
        try {
            it.objectInstance ?: it.java.getDeclaredConstructor().newInstance()
        } catch (ex: ReflectiveOperationException) {
            null
        }
    }

    // Informs about found hosts
    Log.d(if (hosts.isEmpty()) {
        "Found no host to fetch"
    } else {
        "Found following hosts to fetch: " + hosts.joinToString { it.domain }
    })

    // Remove redundant host collections from db
    if (Fetcher.patchRedundancy) {
        val removableCollections = MongoDBConnection.db.listCollectionNames().toMutableList().apply {
            removeAll(hosts.map { it.domain })
        }
        if (removableCollections.isNotEmpty()) {
            Log.w(
                "Following hosts are redundant and will be removed from the database: " +
                        removableCollections.joinToString()
            )
            removableCollections.forEach {
                MongoDBConnection.db.getCollection(it).drop()
            }
        }
    }

    Fetcher.start(hosts)
}

/**
 * Stops the fetching process. Is equivalent to [Fetcher.stop].
 */
fun stopFetcher() {
    Fetcher.stop()
}