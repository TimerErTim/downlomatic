package eu.timerertim.downlomatic.core.fetch

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.net.URL

@Serializable
@SerialName("Plain")
object PlainFetcher : Fetcher() {
    override suspend fun fetch(url: URL) = url
}
