package eu.timerertim.downlomatic.core.meta

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

/**
 * This uniquely identifies episodes/movies.
 *
 * It contains all relevant information to identify a video. Furthermore, [VideoDetails] can be used
 * to create a unique file name and search for a [Video][eu.timerertim.downlomatic.core.video.Video].
 */
@Suppress("EqualsOrHashCode")
@Serializable
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
    val idHash by lazy {
        var result = title?.hashCode() ?: 0
        result = 31 * result + (series?.hashCode() ?: 0)
        result = 31 * result + (season ?: 0)
        result = 31 * result + (episode ?: 0)
        result = 31 * result + (release?.hashCode() ?: 0)
        result = 31 * result + (spokenLanguage?.name.hashCode())
        result = 31 * result + (subtitleLanguage?.name.hashCode())
        result = 31 * result + translation.name.hashCode()
        result = 31 * result + audienceLanguage.name.hashCode()
        result
    }

    override fun toString(): String {
        return "VideoDetails(" +
                "title=$title, " +
                "series=$series, " +
                "season=$season, " +
                "episode=$episode, " +
                "release=$release, " +
                "spokenLanguage=$spokenLanguage, " +
                "subtitleLanguage=$subtitleLanguage, " +
                "translation=$translation, " +
                "audienceLanguage=$audienceLanguage, " +
                "tags=$tags" +
                ")"
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
    var tags: List<Tag> = listOf()
) {
    /**
     * "Builds" this object into an immutable [VideoDetails] object.
     */
    fun build() =
        audienceLanguage?.let {
            VideoDetails(
                title,
                series,
                season,
                episode,
                release,
                spokenLanguage,
                subtitleLanguage,
                translation,
                it,
                tags
            )
        }
            ?: VideoDetails(
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
}
