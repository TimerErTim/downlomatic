package eu.timerertim.downlomatic.core.host

@kotlinx.serialization.Serializable
data class Host(
    val domain: String,
    val isNSFW: Boolean = true
)
