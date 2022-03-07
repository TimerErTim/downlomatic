package eu.timerertim.downlomatic.api

import eu.timerertim.downlomatic.util.Utils
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.serialization.json.Json

val ktorClientLazy = lazy {
    HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
        install(DefaultRequest) {
            this.host = KtorClient.server
            this.port = KtorClient.serverPort
        }
    }
}

object KtorClient {
    var server: String = Utils.DEFAULT_API_SERVER
    var serverPort: Int = Utils.DEFAULT_API_PORT
}

val ktorClient by ktorClientLazy
