package eu.timerertim.downlomatic.hosts

import eu.timerertim.downlomatic.core.scraping.HostScraper

fun main() {
    val tester = HostScraper.Tester(HentaigasmCom)
    tester.test()
}
