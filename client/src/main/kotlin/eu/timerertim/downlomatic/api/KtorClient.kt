package eu.timerertim.downlomatic.api

import eu.timerertim.downlomatic.util.Utils
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*

val ktorClientLazy = lazy {
    HttpClient {
        install(JsonFeature)
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
