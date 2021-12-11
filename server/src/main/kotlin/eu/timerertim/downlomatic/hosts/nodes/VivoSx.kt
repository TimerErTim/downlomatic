package eu.timerertim.downlomatic.hosts.nodes

import eu.timerertim.downlomatic.core.fetch.HostConfig
import eu.timerertim.downlomatic.core.fetch.nodes.*
import eu.timerertim.downlomatic.util.WebScrapers
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import java.net.URL

private class VivoSx(parentNode: ParentNode, url: URL, private val modify: suspend VideoNode.() -> Unit) :
    HostNode(HostConfig(
        4250..7350L,
        requiresJS = true
    ), parentNode, {
        val driver = WebScrapers.javaScript()

        page(url) {
            driver[it.toString()]

            val videoNodes = driver.findElements(By.tagName("video"))
            for (video in videoNodes) {
                var source = try {
                    video.findElement(By.tagName("source")).getAttribute("src")
                } catch (ex: NoSuchElementException) {
                    null
                }
                if (source != null &&
                    source?.startsWith("https://node--")!! && source?.contains("vivo.sx")!! ||
                    video.getAttribute("src")
                        ?.also { source = it } != null &&
                    source?.startsWith("https://node--")!! && source?.contains("vivo.sx")!!
                ) {
                    video(URL(source), modify)
                }

            }
        }
    })


/**
 * Creates a new sub [VideoNode] with a special [VivoSx] implementation. The given [modify] parameter allows
 * performing convenient modifications on the node.
 */
@JvmOverloads
fun ParentNode.videoVivoSx(url: URL, modify: suspend VideoNode.() -> Unit = {}) {
    VivoSx(this, url, modify)
}

