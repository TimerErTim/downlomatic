package eu.timerertim.downlomatic.graphics.window

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp

private const val SCALE_FACTOR = 0.001F
private const val REFERENCE_ASPECT_RATIO = 16 / 9F

val Number.sdp: Dp
    get() {
        val value = this.toFloat()
        val height = DownlomaticWindowState.size.height * REFERENCE_ASPECT_RATIO
        val width = DownlomaticWindowState.size.width
        return min(height, width) * SCALE_FACTOR * value
    }

val Number.ssp get() = this.sdp.value.sp