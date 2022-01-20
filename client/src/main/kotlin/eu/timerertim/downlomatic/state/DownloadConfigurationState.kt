package eu.timerertim.downlomatic.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import eu.timerertim.downlomatic.core.format.VideoDetailsFormatter
import java.io.File

class DownloadConfigurationState {
    val videoFormatState = mutableStateOf("")
    var videoFormat by videoFormatState

    var destinationDirectoryState = mutableStateOf("")
    var destinationDirectory by destinationDirectoryState

    val defaultVideoFormat = VideoDetailsFormatter.DEFAULT_VIDEO_FORMAT
    val defaultDestinationDirectory: String = File("").absolutePath
}
