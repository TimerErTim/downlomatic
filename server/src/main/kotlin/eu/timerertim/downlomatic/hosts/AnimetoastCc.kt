package eu.timerertim.downlomatic.hosts

import eu.timerertim.downlomatic.core.meta.Language
import eu.timerertim.downlomatic.core.meta.Tag
import eu.timerertim.downlomatic.core.meta.Translation
import eu.timerertim.downlomatic.core.parsing.DoodStreamEmbededParser
import eu.timerertim.downlomatic.core.parsing.Mp4UploadComEmbeddedParser
import eu.timerertim.downlomatic.core.parsing.VivoSxParser
import eu.timerertim.downlomatic.core.scraping.HostConfig
import eu.timerertim.downlomatic.core.scraping.HostScraper
import eu.timerertim.downlomatic.core.scraping.nodes.page
import eu.timerertim.downlomatic.core.scraping.nodes.video
import eu.timerertim.downlomatic.util.WebDrivers
import eu.timerertim.downlomatic.util.WebDrivers.get
import kotlinx.coroutines.delay
import org.openqa.selenium.By
import java.net.URL
import kotlin.time.Duration.Companion.seconds

object AnimetoastCc : HostScraper(
    "animetoast.cc", false,
    config = HostConfig(4000L..7500L, requiresJS = true),
    fetch = {
        val driver = WebDrivers.javaScript()

        page(URL("https://www.animetoast.cc/a-z-index/")) {
            driver[it]

            val letterSections = driver.findElements(By.className("letter-section"))
            val seriesLinks = letterSections.flatMap { it.findElements(By.tagName("a")) }

            for (link in seriesLinks) {
                val seriesRef = URL(link.getAttribute("href"))
                page(seriesRef) {
                    driver[it]

                    val header = driver.findElement(By.className("entry-title")).text
                    val sub = header.endsWith(" Ger Sub")
                    val seriesName = header.substringBeforeLast(" Ger")
                    val seriesTags = driver.findElements(By.tagName("a")).mapNotNull {
                        if ((it.getAttribute("rel") ?: return@mapNotNull null) == "tag") {
                            Tag(it.text)
                        } else null
                    }

                    audienceLanguage = Language.GERMAN
                    subtitleLanguage = if (sub) Language.GERMAN else null
                    spokenLanguage = if (sub) Language.JAPANESE else Language.GERMAN
                    translation = if (sub) Translation.SUB else Translation.SUB
                    series = seriesName
                    tags = seriesTags

                    val navs = driver.findElement(By.className("nav-tabs")).findElements(By.tagName("a"))
                    val (nav, parser) = when (listOf("DoodStream", "Mp4Upload", "Vivo").first {
                        it in navs.map { it.text }
                    }) {
                        "DoodStream" -> (navs.first { it.text == "DoodStream" }) to DoodStreamEmbededParser
                        "Mp4Upload" -> (navs.first { it.text == "Mp4Upload" }) to Mp4UploadComEmbeddedParser
                        "Vivo" -> (navs.first { it.text == "Vivo" }) to VivoSxParser
                        else -> throw NoSuchElementException("Series $it has no valid provider")
                    }

                    nav.click()
                    delay(1.seconds)

                    val tabContent = driver.findElement(By.className("tab-content"))
                    val activeTab = tabContent.findElement(By.className("active"))
                    val episodeLinkList = activeTab.findElements(By.tagName("a"))

                    for ((episodeLink, episodeName) in episodeLinkList.map { it.getAttribute("href") to it.text }) {
                        episode = episodeName.removePrefix("Ep. ").toIntOrNull()
                        driver[episodeLink]

                        val player = driver.findElement(By.id("player-embed"))
                        when (parser) {
                            VivoSxParser -> video(
                                URL(player.findElement(By.tagName("a")).getAttribute("href")),
                                parser
                            )
                            Mp4UploadComEmbeddedParser, DoodStreamEmbededParser -> video(
                                URL(player.findElement(By.tagName("iframe")).getAttribute("src")),
                                parser
                            )
                        }
                    }
                }
            }
        }
    }
)
