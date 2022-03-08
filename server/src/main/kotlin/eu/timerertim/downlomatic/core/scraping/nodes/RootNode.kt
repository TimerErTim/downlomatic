package eu.timerertim.downlomatic.core.scraping.nodes

import eu.timerertim.downlomatic.core.meta.VideoDetailsBuilder
import eu.timerertim.downlomatic.core.scraping.HostConfig
import eu.timerertim.downlomatic.core.scraping.HostScraper
import eu.timerertim.downlomatic.util.logging.Log
import kotlinx.coroutines.delay

class RootNode(host: HostScraper, config: HostConfig, private val process: suspend RootNode.() -> Unit) :
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
            Log.e("An error occurred while processing RootNode of host ${scraper.host.domain}", ex)
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

    suspend fun HostScraper._fetch() = this@RootNode.fetch()
}
