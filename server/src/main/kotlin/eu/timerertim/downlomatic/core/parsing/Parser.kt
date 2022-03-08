package eu.timerertim.downlomatic.core.parsing

import kotlinx.serialization.Serializable
import java.net.URL

@Serializable
sealed class Parser : suspend (URL) -> URL {
    abstract suspend fun parse(url: URL): URL

    override suspend operator fun invoke(url: URL) = parse(url)
}
