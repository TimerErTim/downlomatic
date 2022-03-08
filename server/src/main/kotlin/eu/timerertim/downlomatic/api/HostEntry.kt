package eu.timerertim.downlomatic.api

import kotlinx.serialization.Serializable

@Serializable
data class HostEntry(
    val domain: String,
    val isNSFW: Boolean
) {
    val _id = domain
}
