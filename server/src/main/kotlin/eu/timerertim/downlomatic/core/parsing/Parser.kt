package eu.timerertim.downlomatic.core.parsing

import eu.timerertim.downlomatic.core.downloader.Downloader
import eu.timerertim.downlomatic.util.json.DurationSerializer
import kotlinx.serialization.Serializable
import java.net.URL
import kotlin.time.Duration

@Serializable
sealed class Parser : suspend (URL) -> Downloader<*> {
    @Serializable(with = DurationSerializer::class)
    abstract val duration: Duration?

    abstract suspend fun parse(url: URL): Downloader<*>

    override suspend operator fun invoke(url: URL) = parse(url)


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Parser

        if (duration != other.duration) return false

        return true
    }

    override fun hashCode(): Int {
        return duration?.hashCode() ?: 0
    }
}
