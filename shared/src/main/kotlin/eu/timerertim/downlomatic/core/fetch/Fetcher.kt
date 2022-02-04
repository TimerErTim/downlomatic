package eu.timerertim.downlomatic.core.fetch

import kotlinx.serialization.Serializable
import java.net.URL

@Serializable
sealed class Fetcher : suspend (URL) -> URL {
    abstract suspend fun fetch(url: URL): URL

    override suspend operator fun invoke(url: URL) = fetch(url)
}
