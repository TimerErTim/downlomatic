package eu.timerertim.downlomatic.hosts

import eu.timerertim.downlomatic.core.fetch.Host
import eu.timerertim.downlomatic.core.fetch.HostConfig
import eu.timerertim.downlomatic.core.fetch.nodes.page
import eu.timerertim.downlomatic.core.fetch.nodes.video
import eu.timerertim.downlomatic.core.meta.Language
import eu.timerertim.downlomatic.core.meta.Tag
import eu.timerertim.downlomatic.core.meta.Translation
import eu.timerertim.downlomatic.utils.WebScrapers
import org.openqa.selenium.By
import java.net.URL
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.regex.Pattern

object HentaigasmCom : Host("hentaigasm.com",
    HostConfig(
        4000..7500L
    ), {
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
                                        LocalDate.parse(
                                            extras[0].text, DateTimeFormatter.ofPattern("MMMM d, yyyy")
                                                .withZone(ZoneId.of("UTC"))
                                        )
                                    } catch (ex: DateTimeParseException) {
                                        LocalDate.ofYearDay(extras[0].text.split(", ")[1].toInt(), 1)
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