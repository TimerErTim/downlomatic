package eu.timerertim.downlomatic.state

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import eu.timerertim.downlomatic.graphics.component.util.LabeledProgressState

class DownloadStatisticsState(
    calculateSpeed: () -> Long,
    calculateDownloadedBytes: () -> Long,
    calculateFinishedDownloads: () -> Long,
    calculatePlannedDownloads: () -> Long
) {
    val speedState = derivedStateOf(calculateSpeed)
    val speed by speedState

    val downloadsProgressState = LabeledProgressState(
        0, 0,
        nominatorGenerator = calculateFinishedDownloads,
        denominatorGenerator = calculatePlannedDownloads
    ) { amount, of ->
        "$amount / $of Downloads"
    }

    val downloadedBytesState = derivedStateOf(calculateDownloadedBytes)
    val downloadedBytes by downloadedBytesState
}
