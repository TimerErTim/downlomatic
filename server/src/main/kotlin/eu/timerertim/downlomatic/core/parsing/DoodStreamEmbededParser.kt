package eu.timerertim.downlomatic.core.parsing

import eu.timerertim.downlomatic.core.descriptor.HTTPDescriptor
import eu.timerertim.downlomatic.core.downloader.Downloader
import eu.timerertim.downlomatic.core.downloader.HTTPDownloader
import eu.timerertim.downlomatic.util.WebDrivers
import eu.timerertim.downlomatic.util.WebDrivers.get
import kotlinx.coroutines.delay
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.openqa.selenium.By
import java.net.URL
import kotlin.time.Duration.Companion.minutes

@Serializable
@SerialName("DoodStreamEmbededParser")
object DoodStreamEmbededParser : Parser() {
    override val duration = 30.minutes

    override suspend fun parse(url: URL): Downloader<*> {
        val driver = WebDrivers.javaScript()
        driver[url]

        delay(2000)

        val video = driver.findElements(By.tagName("video"))
            .singleOrNull { it.getAttribute("id") == "video_player_html5_api" }
        val videoURL = video?.getAttribute("src")
            ?: throw NoSuchElementException("Video element can not be found")

        val descriptor = HTTPDescriptor(
            URL(videoURL),
            headers = mapOf(
                "Referer" to "https://dood.so/"
            )
        )

        return HTTPDownloader(descriptor)
    }
}
