package eu.timerertim.downlomatic.core.meta

import java.time.LocalDate

/**
 * This uniquely identifies episodes/movies.
 *
 * It contains all relevant information to identify a video. Furthermore, [VideoDetails] can be used
 * to create a unique file name.
 */
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
    } ?: Language.ENGLISH
)