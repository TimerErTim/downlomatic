package eu.timerertim.downlomatic.core.scraping.nodes

import eu.timerertim.downlomatic.core.meta.VideoDetailsBuilder
import eu.timerertim.downlomatic.core.parsing.Parser
import eu.timerertim.downlomatic.core.parsing.PlainParser
import eu.timerertim.downlomatic.core.scraping.HostConfig
import eu.timerertim.downlomatic.core.scraping.HostScraper
import java.net.URL

sealed class Node(
    protected val scraper: HostScraper,
    protected val hostConfig: HostConfig,
    protected val videoDetailsBuilder: VideoDetailsBuilder
) {
    // Properties regarding the VideoDetails
    var title by videoDetailsBuilder::title
    var series by videoDetailsBuilder::series
    var season by videoDetailsBuilder::season
    var episode by videoDetailsBuilder::episode
    var release by videoDetailsBuilder::release
    var spokenLanguage by videoDetailsBuilder::spokenLanguage
    var subtitleLanguage by videoDetailsBuilder::subtitleLanguage
    var translation by videoDetailsBuilder::translation
    var audienceLanguage by videoDetailsBuilder::audienceLanguage
    var tags by videoDetailsBuilder::tags

    constructor(base: Node) : this(base.scraper, base.hostConfig, base.videoDetailsBuilder.copy())

    /**
     * Fetches this node and all potential subnodes for [Video][eu.timerertim.downlomatic.core.video.Video]s.
     */
    protected abstract suspend fun fetch()

    protected companion object {
        suspend fun Node.fetch() = this.fetch()

        val Node.hostConfig get() = this.hostConfig

        val Node.videoDetailsBuilder get() = this.videoDetailsBuilder
    }
}


/**
 * Creates a new sub [PageNode]. The given [fetch] parameter specifies how to retrieve the subnode's children.
 */
fun ParentNode.page(url: URL, fetch: suspend PageNode.(URL) -> Unit) {
    PageNode(this, url, fetch)
}

/**
 * Creates a new sub [VideoNode]. The given [modify] parameter allows performing convenient modifications on the node.
 */
@JvmOverloads
fun ParentNode.video(url: URL, parser: Parser = PlainParser, modify: suspend VideoNode.() -> Unit = {}) {
    VideoNode(this, url, parser, modify)
}
