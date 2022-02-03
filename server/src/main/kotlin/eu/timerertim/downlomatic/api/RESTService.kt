package eu.timerertim.downlomatic.api

import eu.timerertim.downlomatic.api.APIPath.ALL_HOSTS
import eu.timerertim.downlomatic.api.APIPath.ALL_VIDEOS_OF_HOST
import eu.timerertim.downlomatic.api.RESTService.apiPort
import eu.timerertim.downlomatic.core.video.Video
import eu.timerertim.downlomatic.util.MongoDBConnection
import eu.timerertim.downlomatic.util.Utils
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import org.litote.kmongo.getCollection
import java.util.concurrent.TimeUnit

private val ktorEngine by lazy {
    embeddedServer(CIO, apiPort) {
        install(ContentNegotiation) {
            json()
        }

        routing {
            get(ALL_HOSTS.path) {
                val mongoNames = MongoDBConnection.db.listCollectionNames()
                call.respond(mongoNames.toList())
            }
            get(ALL_VIDEOS_OF_HOST.path) {
                val host = call.parameters[ALL_VIDEOS_OF_HOST.HOST_ARGUMENT.name] ?: return@get call.respondText(
                    "Missing or malformed host",
                    status = HttpStatusCode.BadRequest
                )
                if (!MongoDBConnection.db.listCollectionNames().contains(host)) {
                    return@get call.respondText(
                        "No collection of host $host",
                        status = HttpStatusCode.NotFound
                    )
                }
                val videosCollection = MongoDBConnection.db.getCollection<Video>(host)
                val videos = videosCollection.find()
                call.respond(videos.toList())
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
