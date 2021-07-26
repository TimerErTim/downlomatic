package eu.timerertim.downlomatic.core.meta

import java.time.LocalDate

/**
 * This uniquely identifies episodes/movies.
 *
 * It contains all relevant information to identify a video. Furthermore, [VideoDetails] can be used
 * to create a unique file name and search for a [Video][eu.timerertim.downlomatic.core.video.Video].
 */
@Suppress("EqualsOrHashCode")
data class VideoDetails(
    val title: String? = null,
    val series: String? = null,
    val season: Int? = null,
    val episode: Int? = null,
    val release: LocalDate? = null,
    val spokenLanguage: Language? = null,
    val subtitleLanguage: Language? = null,
    val translation: Translation = Translation.OV,
    val audienceLanguage: Language = when (translation) {
        Translation.OV -> spokenLanguage
        Translation.DUB -> spokenLanguage
        Translation.SUB -> subtitleLanguage
    } ?: Language.ENGLISH,
    val tags: List<Tag> = emptyList()
) {
    override fun hashCode(): Int {
        var result = title?.hashCode() ?: 0
        result = 31 * result + (series?.hashCode() ?: 0)
        result = 31 * result + (season ?: 0)
        result = 31 * result + (episode ?: 0)
        result = 31 * result + (release?.hashCode() ?: 0)
        result = 31 * result + (spokenLanguage?.toString().hashCode())
        result = 31 * result + (subtitleLanguage?.toString().hashCode())
        result = 31 * result + translation.toString().hashCode()
        result = 31 * result + audienceLanguage.toString().hashCode()
        result = 31 * result + tags.hashCode()
        return result
    }
}

/**
 * Provides the ability to build a [VideoDetails] object with the ability to copy a already existing configuration and
 * without immediately making it immutable.
 */
data class VideoDetailsBuilder(
    var title: String? = null,
    var series: String? = null,
    var season: Int? = null,
    var episode: Int? = null,
    var release: LocalDate? = null,
    var spokenLanguage: Language? = null,
    var subtitleLanguage: Language? = null,
    var translation: Translation = Translation.OV,
    var audienceLanguage: Language? = null,
    var tags: List<Tag> = emptyList()
) {
    /**
     * "Builds" this object into a immutable [VideoDetails] object.
     */
    fun build() =
        if (audienceLanguage == null) {
            VideoDetailsBuilder(
                title,
                series,
                season,
                episode,
                release,
                spokenLanguage,
                subtitleLanguage,
                translation,
                tags = tags
            )
        } else {
            VideoDetailsBuilder(
                title,
                series,
                season,
                episode,
                release,
                spokenLanguage,
                subtitleLanguage,
                translation,
                audienceLanguage,
                tags
            )
        }
}