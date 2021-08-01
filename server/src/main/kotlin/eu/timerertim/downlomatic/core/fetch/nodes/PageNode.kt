package eu.timerertim.downlomatic.core.fetch.nodes

import eu.timerertim.downlomatic.utils.WebScrapers
import eu.timerertim.downlomatic.utils.logging.Log
import kotlinx.coroutines.delay
import java.net.URI
import java.net.URL

/**
 * This node represents a single page in a website. It can be used to parse data and create child [PageNode]s resulting
 * from that data.
 */
class PageNode(parentNode: ParentNode, url: URL, private val process: suspend PageNode.(URL) -> Unit) :
    Node(
        parentNode as Node
    ), ParentNode, ChildNode {
    private var url = URL(
        URI(url.protocol, url.userInfo, url.host, url.port, url.path, url.query, url.ref)
            .toASCIIString().replace("%25", "%")
    )

    init {
        attachTo(parentNode)
    }

    private val children = mutableListOf<ChildNode>()

    override fun addChild(other: ChildNode) {
        children.add(other)
    }

    override suspend fun fetch() {
        try {
            process(url)
            if (host.config.testing) { // Logging for testing purposes
                Log.d("Processed page \"$url\"")
            }
        } catch (ex: Exception) {
            Log.e("An error occurred while processing URL \"$url\" of host ${host.config.domain}", ex)
            return
        }

        children.forEach {
            if (it is Node) {
                it.fetch() // Fetch child

                // Load parent page after delay
                delay(host.config.delay.random())
                if (host.config.requiresJS) {
                    WebScrapers.javaScript().get(url.toString())
                } else {
                    WebScrapers.noJavaScript().get(url.toString())
                }
                if (host.config.testing) { // Logging for testing purposes
                    Log.d("Automatically loaded page \"$url\"")
                }
                delay(host.config.delay.random())
            }
        }
    }
}