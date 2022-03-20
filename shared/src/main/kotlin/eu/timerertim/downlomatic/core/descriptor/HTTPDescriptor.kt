package eu.timerertim.downlomatic.core.descriptor

import eu.timerertim.downlomatic.util.json.URLSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.net.URL

@Serializable
@SerialName("HTTPDescriptor")
data class HTTPDescriptor(
    @Serializable(URLSerializer::class)
    val url: URL,
    val method: String = "GET",
    val headers: Map<String, String> = emptyMap(),
    val request: String? = null
) : Descriptor()
