package eu.timerertim.downlomatic.core.fetch.nodes

import eu.timerertim.downlomatic.core.fetch.HostConfig
import eu.timerertim.downlomatic.core.meta.VideoDetailsBuilder
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.declaredMemberProperties

/**
 * A HostNode can be extended in order to implement a [Host][eu.timerertim.downlomatic.core.fetch.Host] as [Node] which
 * can be used by other Nodes and therefore Hosts to fetch [Video][eu.timerertim.downlomatic.core.video.Video]s.
 */
abstract class HostNode(config: HostConfig, parentNode: ParentNode, process: suspend RootNode.() -> Unit) :
    Node(parentNode as Node), ChildNode {
    private val root = RootNode(host, config, process)

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

    final override suspend fun fetch() = root.fetch()
}