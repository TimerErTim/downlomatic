package eu.timerertim.downlomatic.core.db

import eu.timerertim.downlomatic.core.host.Host
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HostEntry(
    val host: Host
) {
    @SerialName("_id")
    val id = host.domain
}
