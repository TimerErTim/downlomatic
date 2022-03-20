package eu.timerertim.downlomatic.core.db

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HostEntry(
    @SerialName("_id")
    val domain: String,
    val isNSFW: Boolean
)
