package eu.timerertim.downlomatic.api

import eu.timerertim.downlomatic.core.video.Video

fun Video.toEntry() = VideoEntry(url, fetcher, details)

fun VideoEntry.toVideo() = Video(url, fetcher, details)
