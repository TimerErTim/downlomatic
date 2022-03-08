package eu.timerertim.downlomatic.core.scraping.nodes

import eu.timerertim.downlomatic.core.meta.VideoDetailsBuilder
import eu.timerertim.downlomatic.core.scraping.HostConfig
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.declaredMemberProperties

/**
 * A HostNode can be extended in order to implement a [Host][eu.timerertim.downlomatic.core.parsing.HostScraper] as [Node] which
 * can be used by other Nodes and therefore Hosts to fetch [Video][eu.timerertim.downlomatic.core.video.Video]s.
 */
abstract class HostNode(config: HostConfig, parentNode: ParentNode, process: suspend RootNode.() -> Unit) :
    Node(parentNode as Node), ChildNode {
    private val root = RootNode(scraper, config.copy(testing = hostConfig.testing), process)

    init {
        this.attachTo(parentNode)

        // "Copy" current VideoDetailsBuilder to root
        for (property in VideoDetailsBuilder::class.declaredMemberProperties) {
            if (property is KMutableProperty1) {
                @Suppress("UNCHECKED_CAST")
                (property as KMutableProperty1<VideoDetailsBuilder, Any?>)
                    .set(root.videoDetailsBuilder, property.get(this.videoDetailsBuilder))
            }
        }
    }

    public final override suspend fun fetch() = root.fetch()
}
