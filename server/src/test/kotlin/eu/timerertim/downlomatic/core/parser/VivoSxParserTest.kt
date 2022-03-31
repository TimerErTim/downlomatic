package eu.timerertim.downlomatic.core.parser

import eu.timerertim.downlomatic.core.parsing.VivoSxParser
import java.net.URL

suspend fun main() {
    val parser = VivoSxParser
    println(parser(URL("https://vivo.sx/d4de0ce22b")))
}
