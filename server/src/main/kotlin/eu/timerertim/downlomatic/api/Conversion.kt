package eu.timerertim.downlomatic.api

import eu.timerertim.downlomatic.core.db.DownloaderEntry
import eu.timerertim.downlomatic.core.db.HostEntry
import eu.timerertim.downlomatic.core.db.VideoEntry
import eu.timerertim.downlomatic.core.downloader.Downloader
import eu.timerertim.downlomatic.core.host.Host
import eu.timerertim.downlomatic.core.parsing.Parser
import eu.timerertim.downlomatic.core.video.Video
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant

fun Downloader<*>.toEntry(id: Long, expireAt: Instant?) = DownloaderEntry(id, expireAt?.toJavaInstant(), this)

fun DownloaderEntry.toDownloader() = downloader

fun Video.toEntry(parser: Parser) = VideoEntry(parser, this)

fun VideoEntry.toVideo() = video

fun Host.toEntry() = HostEntry(this)

fun HostEntry.toHost() = host
