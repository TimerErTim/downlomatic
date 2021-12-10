package eu.timerertim.downlomatic.core.fetch

/**
 * This class is used as a hostConfig class for [Host]s by containing essential
 * information about the [Host].
 * - [delay]: The delay range describing minimum and maximum delay in ms between processing two
 * [Node][eu.timerertim.downlomatic.core.fetch.nodes.Node]s.
 * - [requiresJS]: Defines the type of [WebScraper][eu.timerertim.downlomatic.utils.WebScrapers] to use when loading a
 * webpage behind the scenes.
 * - [defaultFileType]: The filetype the host falls back to when the real filetype of a video can't be determined.
 */
data class HostConfig @JvmOverloads constructor(
    val delay: LongRange,
    val requiresJS: Boolean = false,
    val defaultFileType: String = "mp4"
) {
    var testing: Boolean = false
        private set

    fun copy(
        delay: LongRange = this.delay,
        requiresJS: Boolean = this.requiresJS,
        defaultFileType: String = this.defaultFileType,
        testing: Boolean = this.testing
    ) =
        HostConfig(delay, requiresJS, defaultFileType).apply { this.testing = testing }

    fun Host.Tester._setTesting(value: Boolean) {
        testing = value
    }
}