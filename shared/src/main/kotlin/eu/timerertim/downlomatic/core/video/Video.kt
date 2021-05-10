package eu.timerertim.downlomatic.core.video

import eu.timerertim.downlomatic.core.meta.VideoDetails
import java.net.URL

data class Video(val url: URL, val details: VideoDetails, val metadata: Metadata)