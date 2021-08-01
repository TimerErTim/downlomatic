package eu.timerertim.downlomatic.core.fetch

/**
 * This class is used as a config class for [Host]s by containing essential
 * information about the [Host].
 * - [domain]: The public host name. The Host will be registered with this name internally. As suggested by its name,
 * this property should be the domain of the host.
 * - [delay]: The delay range describing minimum and maximum delay in ms between processing two
 * [Node][eu.timerertim.downlomatic.core.fetch.nodes.Node]s.
 * - [requiresJS]: Defines the type of [WebScraper][eu.timerertim.downlomatic.utils.WebScrapers] to use when loading a
 * webpage behind the scenes.
 * - [defaultFileType]: The filetype the host falls back to when the real filetype of a video can't be determined.
 */
data class HostConfig @JvmOverloads constructor(
    val domain: String,
    val delay: LongRange,
    val requiresJS: Boolean = false,
    val defaultFileType: String = "mp4"
) {
    var testing: Boolean = false
        private set

    fun Host.Tester._setTesting(value: Boolean) {
        testing = value
    }
}