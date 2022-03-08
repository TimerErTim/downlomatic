package eu.timerertim.downlomatic.hosts

import eu.timerertim.downlomatic.core.meta.Language
import eu.timerertim.downlomatic.core.meta.Tag
import eu.timerertim.downlomatic.core.meta.Translation
import eu.timerertim.downlomatic.core.scraping.HostConfig
import eu.timerertim.downlomatic.core.scraping.HostScraper
import eu.timerertim.downlomatic.core.scraping.nodes.page
import eu.timerertim.downlomatic.core.scraping.nodes.video
import eu.timerertim.downlomatic.util.WebScrapers
import kotlinx.datetime.toKotlinLocalDate
import org.openqa.selenium.By
import java.net.MalformedURLException
import java.net.URL
import java.time.LocalDate

object HentaiplayNet : HostScraper(
    "hentaiplay.net", true,
    config = HostConfig(
        5250..9500L
    ),
    fetch = {
        val driver = WebScrapers.noJavaScript()
        driver["https://hentaiplay.net/hentai-index/"]

        // Parse Series
        val seriesWrappers = driver.findElements(By.className("serieslist-content"))
        for (wrapper in seriesWrappers) {
            val link = wrapper.findElement(By.tagName("a"))
            if (link != null) {
                val url = try {
                    URL(link.getAttribute("href"))
                } catch (ignored: MalformedURLException) {
                    continue
                }

                page(url) {
                    driver[it.toString()]

                    // Parse Episodes
                    val seriesElements = driver.findElements(By.tagName("a"))
                    for (seriesElement in seriesElements) {
                        val episodeURL = try {
                            if ("bookmark" == seriesElement.getAttribute("rel")) {
                                URL(seriesElement.getAttribute("href"))
                            } else continue
                        } catch (ignored: MalformedURLException) {
                            continue
                        }

                        page(episodeURL) {
                            driver[it.toString()]

                            // Parse VideoDetails
                            val titleElement = driver.findElement(By.className("entry-title"))
                            var temp =
                                titleElement!!.text.split(" Episode ".toRegex()).toTypedArray().takeIf { it.size > 1 }
                                    ?: arrayOf(
                                        titleElement.text.split(" ").dropLast(2).joinToString(" "),
                                        titleElement.text.split(" ").takeLast(2).joinToString(" ")
                                    )
                            series = temp[0]
                            temp = temp[1].split(" ".toRegex()).toTypedArray()
                            try {
                                episode = temp[0].toInt()
                            } catch (ex: NumberFormatException) {
                                title = temp[0]
                            }
                            spokenLanguage = Language.JAPANESE
                            if (temp.size > 1 && temp[1] == "English") {
                                subtitleLanguage = Language.ENGLISH
                                translation = Translation.SUB
                            }
                            val tagList = mutableListOf<Tag>()
                            driver.findElement(By.id("extras"))?.findElements(By.tagName("a"))?.filter {
                                it.getAttribute("rel") == "tag"
                            }?.forEachIndexed { index, webElement ->
                                if (index == 0 && try {
                                        release = LocalDate
                                            .ofYearDay(webElement.text.toInt(), 1)
                                            .toKotlinLocalDate()
                                        true
                                    } catch (ex: NumberFormatException) {
                                        false
                                    }
                                ) else {
                                    tagList += Tag(webElement.text)
                                }
                            }
                            tags = tagList

                            // Parse Video
                            val episodeElements = driver.findElements(By.id("my-video")).toMutableList()
                            episodeElements.addAll(driver.findElements(By.id("my_video_1")))
                            for (episodeElement in episodeElements) {
                                val source = episodeElement.findElement(By.tagName("source"))
                                if (source != null && (source.getAttribute("src")
                                        .startsWith("https://hentaiplanet.info/") || source.getAttribute("src")
                                        .startsWith("https://openload.co/embed/"))
                                ) {
                                    val videoURL = try {
                                        URL(source.getAttribute("src"))
                                    } catch (ignored: MalformedURLException) {
                                        continue
                                    }

                                    video(videoURL)
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }
    })
