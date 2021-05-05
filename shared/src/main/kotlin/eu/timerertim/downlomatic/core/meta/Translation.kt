package eu.timerertim.downlomatic.core.meta

/**
 * This enum contains valid translation methods usable in [VideoDetails].
 */
enum class Translation {
    /**
     * Indicates that the audience language was achieved using subtitles.
     */
    SUB,

    /**
     * Indicates that the audience language was achieved using audio synchro.
     */
    DUB,

    /**
     * Indicates that the audience language was already achieved because of it being the original voice.
     */
    OV
}