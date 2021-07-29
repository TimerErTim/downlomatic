package eu.timerertim.downlomatic.core.fetch.nodes

import eu.timerertim.downlomatic.core.fetch.Host
import eu.timerertim.downlomatic.core.meta.VideoDetailsBuilder
import java.net.URL

sealed class Node(protected val host: Host, protected val videoDetailsBuilder: VideoDetailsBuilder) {
    // Properties regarding the VideoDetails
    var title by this.videoDetailsBuilder::title
    var series by this.videoDetailsBuilder::series
    var season by this.videoDetailsBuilder::season
    var episode by this.videoDetailsBuilder::episode
    var release by this.videoDetailsBuilder::release
    var spokenLanguage by this.videoDetailsBuilder::spokenLanguage
    var subtitleLanguage by this.videoDetailsBuilder::subtitleLanguage
    var translation by this.videoDetailsBuilder::translation
    var audienceLanguage by this.videoDetailsBuilder::audienceLanguage
    var tags by this.videoDetailsBuilder::tags

    constructor(base: Node) : this(base.host, base.videoDetailsBuilder.copy())

    /**
     * Fetches this node and all potential subnodes for [Video][eu.timerertim.downlomatic.core.video.Video]s.
     */
    protected abstract suspend fun fetch()

    protected companion object {
        suspend fun Node.fetch() = this.fetch()
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
fun ParentNode.video(url: URL, modify: suspend VideoNode.() -> Unit = {}) {
    VideoNode(this, url, modify)
}