package eu.timerertim.downlomatic.api

import io.ktor.client.*
import io.ktor.client.features.json.*

val ktorClientLazy = lazy {
    HttpClient {
        install(JsonFeature)
    }
}

val ktorClient by ktorClientLazy
