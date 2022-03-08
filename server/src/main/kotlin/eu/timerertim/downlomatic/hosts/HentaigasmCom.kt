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
import java.net.URL
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

object HentaigasmCom : HostScraper(
    "hentaigasm.com", true,
    config = HostConfig(
        4000..7500L
    ),
    fetch = {
        val driver = WebScrapers.noJavaScript()

        var reachedLastPage = false

        for (count in 1..200) {
            page(URL("http://hentaigasm.com/" + if (count > 1) "page/$count/" else "")) {
                if (reachedLastPage) {
                    return@page
                } else {
                    reachedLastPage = true
                }

                driver[it.toString()]

                // Search for episodes in one of main pages
                val pageElements = driver.findElements(By.tagName("a"))

                for (pageElement in pageElements) {
                    if ("bookmark" == pageElement.getAttribute("rel")) {
                        val link = pageElement.getAttribute("href")
                        if (!link.contains("hentaigasm.com/2021/05/26/uncensored-2-subbed")) {

                            page(URL(link)) {
                                driver[it.toString()]

                                // Parse VideoDetails
                                var element = driver.findElement(By.id("headline"))?.findElement(By.id("title"))
                                val title = element?.text ?: return@page
                                val seriesName = title.replace("\\d+ (Subbed|Raw)".toRegex(), "")
                                val postSeriesName =
                                    title.replace(Pattern.quote(seriesName).toRegex(), "").split(" ".toRegex())
                                        .toTypedArray()
                                episode = postSeriesName[0].toInt()
                                spokenLanguage = Language.JAPANESE
                                if (postSeriesName[1].equals(
                                        "Subbed",
                                        ignoreCase = true
                                    )
                                ) {
                                    subtitleLanguage = Language.ENGLISH
                                    translation = Translation.SUB
                                }
                                series = seriesName.substring(0, seriesName.length - 1)
                                val extras = driver.findElement(By.id("extras"))?.findElements(By.tagName("h4"))
                                if (extras != null) {
                                    release = try {
                                        LocalDate
                                            .parse(
                                                extras[0].text,
                                                DateTimeFormatter.ofPattern("MMMM d, yyyy").withZone(ZoneId.of("UTC"))
                                            )
                                            .toKotlinLocalDate()
                                    } catch (ex: IllegalArgumentException) {
                                        LocalDate
                                            .ofYearDay(extras[0].text.split(", ")[1].toInt(), 1)
                                            .toKotlinLocalDate()
                                    }
                                    tags = extras[2].findElements(By.tagName("a")).filter {
                                        it.getAttribute("rel") == "tag"
                                    }.map { Tag(it.text) }
                                }

                                // Parse Video
                                element = driver.findElements(By.tagName("a"))
                                    .firstOrNull { it.getAttribute("download") != null }
                                element?.let { video(URL(it.getAttribute("href"))) }
                            }
                        }

                        reachedLastPage = false
                    }
                }
            }
        }
    })
