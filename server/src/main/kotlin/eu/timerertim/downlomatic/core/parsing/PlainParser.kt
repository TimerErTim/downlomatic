package eu.timerertim.downlomatic.core.parsing

import eu.timerertim.downlomatic.core.descriptor.HTTPDescriptor
import eu.timerertim.downlomatic.core.downloader.HTTPDownloader
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.net.URL
import kotlin.time.Duration

@Serializable
@SerialName("PlainParser")
object PlainParser : Parser() {
    override val duration: Duration? = null

    override suspend fun parse(url: URL): HTTPDownloader =
        HTTPDownloader(HTTPDescriptor(url = url))
}
