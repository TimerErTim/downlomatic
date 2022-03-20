package eu.timerertim.downlomatic.hosts

import eu.timerertim.downlomatic.core.scraping.HostConfig
import eu.timerertim.downlomatic.core.scraping.HostScraper

object AnimixplayTo : HostScraper("animixplay.to",
    config = HostConfig(4250L..7250L, true),
    fetch = {
        /*val driver = WebDrivers.javaScript()

        page(URL("https://animixplay.to/list")) {
            driver[it]

            val typeselect = try {
                driver.findElement(By.id("typeselect"))?.let { Select(it) }
            } catch (ex: UnexpectedTagNameException) {
                null
            }
            typeselect?.selectByValue("any")
            typeselect?.selectByValue("1")

            val alphabeticalListDiv = driver.findElement(By.id("alphabetical"))
            val alphabeticalButtons = alphabeticalListDiv.findElements(By.tagName("button"))

            for (alphabeticalButton in alphabeticalButtons) {
                alphabeticalButton.click()
                val seriesDivs = driver.findElements(By.className("allitem"))
                for (div in seriesDivs) {
                    val seriesLink = div.findElement(By.tagName("a"))
                    val hasDubTag = seriesLink.findElements(By.className("dubtag")).isNotEmpty()
                    val seriesName = if (hasDubTag) seriesLink.text.removeSuffix(" [Dub]") else seriesLink.text

                    series = seriesName
                    translation = if (hasDubTag) Translation.DUB else Translation.DUB
                    audienceLanguage = Language.ENGLISH
                    spokenLanguage = if (hasDubTag) Language.ENGLISH else Language.JAPANESE
                    subtitleLanguage = if (hasDubTag) null else Language.ENGLISH

                    page(URL(seriesLink.getAttribute("href"))) {
                        driver[it]

                        val episodeListDiv = driver.findElement(By.id("epslistplace"))
                        val episodeButtons = episodeListDiv.findElements(By.tagName("button"))

                        for (episodeButton in episodeButtons) {
                            episodeButton.click()

                            val downloadButton = driver.findElements(By.tagName("i")).find {
                                "dlbutton" in it.getAttribute("class")
                            }
                            downloadButton?.click()

                            driver.switchTo().frame(0)
                        }
                    }
                }
            }
        }*/
    }
)
