package eu.timerertim.downlomatic.api

import eu.timerertim.downlomatic.api.APIPath.*
import eu.timerertim.downlomatic.api.RESTService.apiPort
import eu.timerertim.downlomatic.core.db.HostEntry
import eu.timerertim.downlomatic.core.db.VideoEntry
import eu.timerertim.downlomatic.core.host.Host
import eu.timerertim.downlomatic.core.video.Video
import eu.timerertim.downlomatic.util.Utils
import eu.timerertim.downlomatic.util.db.MongoDB
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
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import org.litote.kmongo.div
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.findOneById
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
                getDownloaderOfVideo()
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
        val hostEntries = MongoDB.hostCollection.find()
        val hosts = hostEntries.map { it.toHost() }.toList()
        call.respond(hosts)
    }
}

private fun Route.getAllVideosOfHost() {
    get(ALL_VIDEOS_OF_HOST) {
        val hostName = call.parameters[ALL_VIDEOS_OF_HOST.HOST_ARGUMENT] ?: return@get call.respondText(
            "Missing or malformed host parameter",
            status = HttpStatusCode.BadRequest
        )
        MongoDB.hostCollection.findOne(HostEntry::host / Host::domain eq hostName) ?: return@get call.respondText(
            "No host $hostName available",
            status = HttpStatusCode.NotFound
        )
        val videosCollection = MongoDB.videoCollection.find(VideoEntry::video / Video::host / Host::domain eq hostName)
        val videos = videosCollection.map(VideoEntry::toVideo).toList()
        call.respond(videos)
    }
}

private fun Route.getAllVideos() {
    get(ALL_VIDEOS) {
        val videoEntries = MongoDB.videoCollection.find()
        val videos = videoEntries.map(VideoEntry::toVideo).toList()
        call.respond(videos)
    }
}

private fun Route.getDownloaderOfVideo() {
    get(DOWNLOADER_OF_VIDEO) {
        val videoIDParameter = call.parameters[DOWNLOADER_OF_VIDEO.VIDEO_ID] ?: return@get call.respondText(
            "Missing or malformed video id parameter",
            status = HttpStatusCode.BadRequest
        )
        val videoID = videoIDParameter.toLongOrNull() ?: return@get call.respondText(
            "Wrong datatype for video id parameter: has to be number",
            status = HttpStatusCode.BadRequest
        )
        val videoEntry = MongoDB.videoCollection.findOneById(videoID) ?: return@get call.respondText(
            "No video $videoID available",
            status = HttpStatusCode.NotFound
        )

        val downloader = MongoDB.downloaderCollection.findOneById(videoID)?.toDownloader() ?: try {
            val parser = videoEntry.parser
            val downloader = parser(videoEntry.video.url)
            val duration = parser.duration
            val instant = if (duration != null) Clock.System.now() + duration else null
            val downloaderEntry = downloader.toEntry(videoID, instant)
            MongoDB.downloaderCollection.insertOne(downloaderEntry)
            downloader
        } catch (ex: Exception) {
            return@get call.respondText(
                ex.message ?: "Error occurred",
                status = HttpStatusCode.InternalServerError
            )
        }

        call.respond(downloader)
    }
}
