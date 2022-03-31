package eu.timerertim.downlomatic.core.parsing

import eu.timerertim.downlomatic.core.descriptor.HTTPDescriptor
import eu.timerertim.downlomatic.core.downloader.Downloader
import eu.timerertim.downlomatic.core.downloader.HTTPDownloader
import eu.timerertim.downlomatic.util.WebDrivers
import eu.timerertim.downlomatic.util.WebDrivers.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.openqa.selenium.By
import java.net.URL
import kotlin.time.Duration.Companion.hours

@Serializable
@SerialName("Mp4UploadComEmbeddedParser")
object Mp4UploadComEmbeddedParser : Parser() {
    override val duration = 3.hours

    override suspend fun parse(url: URL): Downloader<*> {
        val driver = WebDrivers.javaScript()
        driver[url]

        val video = driver.findElement(By.tagName("video"))
        val videoURL = video.getAttribute("src")

        val descriptor = HTTPDescriptor(
            URL(videoURL),
            headers = mapOf(
                "Referer" to "https://www.mp4upload.com/"
            )
        )

        return HTTPDownloader(descriptor)
    }
}
