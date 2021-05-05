package eu.timerertim.downlomatic.core.meta

/**
 * This class contains all metadata information about a video.
 *
 * It contains a list of [Tag]s and a [VideoDetails] object, describing exactly what video this [Metadata]
 * belongs to.
 */
data class Metadata(val details: VideoDetails, val tags: List<Tag>)