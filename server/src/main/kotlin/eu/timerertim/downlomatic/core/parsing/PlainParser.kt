package eu.timerertim.downlomatic.core.parsing

import eu.timerertim.downlomatic.core.descriptor.HTTPDescriptor
import eu.timerertim.downlomatic.core.downloader.HTTPDownloader
import eu.timerertim.downlomatic.util.json.DurationSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.net.URL
import kotlin.time.Duration

@Serializable
@SerialName("PlainParser")
data class PlainParser(
    @Serializable(with = DurationSerializer::class)
    override val duration: Duration? = null
) : Parser() {

    override suspend fun parse(url: URL): HTTPDownloader =
        HTTPDownloader(HTTPDescriptor(url = url))
}
