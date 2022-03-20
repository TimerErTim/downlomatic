package eu.timerertim.downlomatic.core.parsing

import eu.timerertim.downlomatic.core.descriptor.HTTPDescriptor
import eu.timerertim.downlomatic.core.downloader.HTTPDownloader
import eu.timerertim.downlomatic.util.WebDrivers
import eu.timerertim.downlomatic.util.WebDrivers.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.openqa.selenium.By
import java.net.URL
import kotlin.time.Duration.Companion.hours

@Serializable
@SerialName("VivoSxParser")
object VivoSxParser : Parser() {
    override val duration = 12.hours

    @Throws(NoSuchElementException::class)
    override suspend fun parse(url: URL): HTTPDownloader {
        val driver = WebDrivers.javaScript()

        driver[url]

        val videoNodes = driver.findElements(By.tagName("video"))
        for (video in videoNodes) {
            var source = try {
                video.findElement(By.tagName("source")).getAttribute("src")
            } catch (ex: NoSuchElementException) {
                null
            }
            if (source != null &&
                source.startsWith("https://node--") && source.contains("vivo.sx") ||
                video.getAttribute("src")
                    ?.also { source = it } != null &&
                source?.startsWith("https://node--")!! && source?.contains("vivo.sx")!!
            ) {
                val url = URL(source)
                return HTTPDownloader(HTTPDescriptor(url = url))
            }
        }

        throw NoSuchElementException("$url can not be found on vivo.sx")
    }
}
