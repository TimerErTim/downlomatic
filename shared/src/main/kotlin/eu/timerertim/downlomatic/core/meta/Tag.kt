package eu.timerertim.downlomatic.core.meta

import kotlinx.serialization.Serializable

/**
 * A [Tag] is a simple bonus attribute. It can contain genre types, keywords or other relevant terms which the [VideoDetails]
 * may be searchable and findable for.
 */
@Serializable
@JvmInline
value class Tag(val value: String)
