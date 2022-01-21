package eu.timerertim.downlomatic.api

import io.ktor.client.request.*

suspend fun requestHosts(): List<String> {
    return ktorClient.get(APIPath.ALL_HOSTS.query())
}
