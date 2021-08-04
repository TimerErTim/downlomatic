package eu.timerertim.downlomatic.core.fetch.nodes

import eu.timerertim.downlomatic.core.fetch.Host
import eu.timerertim.downlomatic.core.fetch.HostConfig
import eu.timerertim.downlomatic.core.meta.VideoDetailsBuilder
import eu.timerertim.downlomatic.utils.logging.Log
import kotlinx.coroutines.delay

class RootNode(host: Host, config: HostConfig, private val process: suspend RootNode.() -> Unit) :
    Node(host, config, VideoDetailsBuilder()),
    ParentNode {
    private val children = mutableListOf<ChildNode>()

    override fun addChild(other: ChildNode) {
        children.add(other)
    }

    override suspend fun fetch() {
        try {
            process()
        } catch (ex: Exception) {
            Log.e("An error occurred while processing RootNode of host ${host.domain}", ex)
            return
        }

        children.forEachIndexed { index, childNode ->
            if (childNode is Node) {
                if (index > 0) {
                    delay(hostConfig.delay.random())
                }
                childNode.fetch()
            }
        }
    }

    suspend fun Host._fetch() = this@RootNode.fetch()
}