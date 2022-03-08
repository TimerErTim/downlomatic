package eu.timerertim.downlomatic.api

import eu.timerertim.downlomatic.api.APIPath.*
import eu.timerertim.downlomatic.api.RESTService.apiPort
import eu.timerertim.downlomatic.util.MongoDBConnection
import eu.timerertim.downlomatic.util.Utils
import eu.timerertim.downlomatic.util.routing.get
import guru.zoroark.ratelimit.RateLimit
import guru.zoroark.ratelimit.rateLimited
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kotlinx.serialization.json.Json
import org.litote.kmongo.getCollection
import java.util.concurrent.TimeUnit

private val ktorEngine by lazy {
    embeddedServer(CIO, apiPort) {
        install(ContentNegotiation) {
            json(Json {
                encodeDefaults = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(RateLimit)

        routing {
            rateLimited {
                getAllHosts()
                getAllVideos()
                getAllVideosOfHost()
            }
            rateLimited(100L) {
                getURLOfVideo()
            }
        }
    }
}

object RESTService {
    var apiPort = Utils.DEFAULT_API_PORT
}

/**
 * Starts Ktor listening on the port [apiPort].
 */
fun startKtor() {
    ktorEngine.start()
}

/**
 * Stops Ktor, waiting at max 5 seconds before forcefully shutting down.
 */
fun stopKtor() {
    ktorEngine.stop(1, 5, TimeUnit.SECONDS)
}

private fun Route.getAllHosts() {
    get(ALL_HOSTS) {
        val hostEntries = MongoDBConnection.hostDB.getCollection<HostEntry>("hosts").find()
        val hosts = hostEntries.map { it.toHost() }.toList()
        call.respond(hosts)
    }
}

private fun Route.getAllVideosOfHost() {
    get(ALL_VIDEOS_OF_HOST) {
        val host = call.parameters[ALL_VIDEOS_OF_HOST.HOST_ARGUMENT] ?: return@get call.respondText(
            "Missing or malformed host",
            status = HttpStatusCode.BadRequest
        )
        if (!MongoDBConnection.videoDB.listCollectionNames().contains(host)) {
            return@get call.respondText(
                "No collection of host $host",
                status = HttpStatusCode.NotFound
            )
        }
        val videosCollection = MongoDBConnection.videoDB.getCollection<VideoEntry>(host)
        val videos = videosCollection.find().map(VideoEntry::toVideo).toList()
        call.respond(videos)
    }
}

private fun Route.getAllVideos() {
    get(ALL_VIDEOS) {
        val hostNames = MongoDBConnection.videoDB.listCollectionNames().toList()
        val videoEntries = hostNames.flatMap { MongoDBConnection.videoDB.getCollection<VideoEntry>(it).find() }
        val videos = videoEntries.map(VideoEntry::toVideo)
        call.respond(videos)
    }
}

private fun Route.getURLOfVideo() {

}
