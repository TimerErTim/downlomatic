package eu.timerertim.downlomatic.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import eu.timerertim.downlomatic.core.format.VideoDetailsFormatter
import java.io.File

class DownloadConfigurationState {
    val videoFormatState = mutableStateOf<String?>(null)
    var videoFormat by videoFormatState
    val videoFormatOrDefault get() = videoFormat ?: defaultVideoFormat

    var destinationDirectoryState = mutableStateOf<String?>(null)
    var destinationDirectory by destinationDirectoryState
    val destinationDirectoryOrDefault get() = destinationDirectory ?: defaultDestinationDirectory

    val defaultVideoFormat = VideoDetailsFormatter.DEFAULT_PATH_FORMAT
    val defaultDestinationDirectory: String = File("").absolutePath
}
