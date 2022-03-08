package eu.timerertim.downlomatic.api

import eu.timerertim.downlomatic.core.host.Host
import eu.timerertim.downlomatic.core.parsing.Parser
import eu.timerertim.downlomatic.core.video.Video

fun Video.toEntry(fetcher: Parser) = VideoEntry(url, fetcher, details)

fun VideoEntry.toVideo() = Video(url, details)

fun Host.toEntry() = HostEntry(domain, isNSFW)

fun HostEntry.toHost() = Host(domain, isNSFW)
