package eu.timerertim.downlomatic.core.parsing

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.net.URL

@Serializable
@SerialName("Plain")
object PlainParser : Parser() {
    override suspend fun parse(url: URL) = url
}
