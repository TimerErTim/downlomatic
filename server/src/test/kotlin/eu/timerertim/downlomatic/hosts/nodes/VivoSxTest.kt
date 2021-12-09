package eu.timerertim.downlomatic.hosts.nodes

import eu.timerertim.downlomatic.core.fetch.Host
import eu.timerertim.downlomatic.core.fetch.HostConfig
import java.net.URL

fun main() {
    val host = object : Host("vivo.sx", HostConfig(1000..1000L), {
        videoVivoSx(URL("https://vivo.sx/dbf8c32926"))
    }) {}
    val tester = Host.Tester(host)
    tester.test() // Should print something along the line of nanatsu no taizai
    // Checksum is 78e2e562508ff8a93e1ab0aad5bcbdda5d86946df643783567df32860e4b6500c9204ecae5c8b9bc752a927f729936d8c55e0094fea0bba4eefd7511b2d89b88
    // Equals the one of the downloaded file
}