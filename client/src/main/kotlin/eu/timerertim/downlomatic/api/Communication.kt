package eu.timerertim.downlomatic.api

import eu.timerertim.downlomatic.core.video.Video
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.net.PortUnreachableException

var requestHostsTries = 0

suspend fun requestHosts(): List<String> {
    delay(2000)
    try {
        if (requestHostsTries < 0) throw PortUnreachableException("Server is not reachable")
        return listOf("Hentaigasm.com", "Hentaiplay.net")
    } finally {
        requestHostsTries++
    }
}

var requestVideosTries = 0

suspend fun requestVideos(host: String): List<Video> {
    delay(1000)
    try {
        if (requestVideosTries < 0) throw PortUnreachableException("Server is not reachable")
        val videosFile = when (host) {
            "Hentaigasm.com" -> "hentaigasm"
            "Hentaiplay.net" -> "hentaiplay"
            else -> throw IllegalArgumentException("Invalid host")
        }
        return Json.decodeFromStream(File("logs/$videosFile.json").inputStream())
    } finally {
        requestVideosTries++
    }
}
