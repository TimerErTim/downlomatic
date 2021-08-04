package eu.timerertim.downlomatic.hosts.nodes

import eu.timerertim.downlomatic.core.fetch.HostConfig
import eu.timerertim.downlomatic.core.fetch.nodes.HostNode
import eu.timerertim.downlomatic.core.fetch.nodes.ParentNode
import eu.timerertim.downlomatic.core.fetch.nodes.VideoNode
import java.net.URL

private class VivoSx(parentNode: ParentNode, url: URL, private val modify: suspend VideoNode.() -> Unit) :
    HostNode(HostConfig(
        4250..7350L,
        requiresJS = true
    ), parentNode, {

    })


/**
 * Creates a new sub [VideoNode] with a special [VivoSx] implementation. The given [modify] parameter allows
 * performing convenient modifications on the node.
 */
@JvmOverloads
fun ParentNode.videoVivoSx(url: URL, modify: suspend VideoNode.() -> Unit = {}) {
    VivoSx(this, url, modify)
}

