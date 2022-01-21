package eu.timerertim.downlomatic.api

import com.fasterxml.jackson.annotation.JsonFilter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import eu.timerertim.downlomatic.api.APIPath.ALL_HOSTS
import eu.timerertim.downlomatic.api.APIPath.ALL_VIDEOS_OF_HOST
import eu.timerertim.downlomatic.core.meta.VideoDetails
import eu.timerertim.downlomatic.core.video.Video
import eu.timerertim.downlomatic.util.MongoDBConnection
import eu.timerertim.downlomatic.util.Utils
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import org.litote.kmongo.getCollection
import java.util.concurrent.TimeUnit

private val ktorEngine = embeddedServer(CIO, Utils.KTOR_PORT) {
    install(ContentNegotiation) {
        jackson {
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            registerModule(JavaTimeModule())
            addMixIn(Any::class.java, RESTFiler::class.java)
            setFilterProvider(
                SimpleFilterProvider().addFilter(
                    "RESTFilter",
                    SimpleBeanPropertyFilter.serializeAllExcept(Video::_id.name, VideoDetails::idHash.name)
                )
            )
        }
    }
    routing {
        get(ALL_HOSTS.path) {
            call.respond(MongoDBConnection.db.listCollectionNames())
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
            val videos = MongoDBConnection.db.getCollection<Video>(host)
            call.respond(videos.find().toList())
        }
    }
}

/**
 * Starts Ktor listening on the port
 * declared under [Utils][eu.timerertim.downlomatic.util.Utils].
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

@JsonFilter("RESTFilter")
interface RESTFiler