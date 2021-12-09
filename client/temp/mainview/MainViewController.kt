package eu.timerertim.downlomatic.graphics.mainview

import eu.timerertim.downlomatic.graphics.SimpleDeltaLongProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleLongProperty
import tornadofx.Controller
import tornadofx.*
import tornadofx.plusAssign
import tornadofx.times
import kotlin.concurrent.thread

/**
 * The [Controller] for the [MainView].
 */
class MainViewController : Controller() {
    val downloadedBytes = SimpleLongProperty(0)
    val downloadedDeltaBytes = SimpleDeltaLongProperty(downloadedBytes, 1500)
    val totalBytes = SimpleLongProperty(0)
    val downloadedCount = SimpleIntegerProperty(0)
    val totalCount = SimpleIntegerProperty(0)

    val totalByteProgress = SimpleDoubleProperty().apply { bind(downloadedBytes * 1.0 / totalBytes) }
    val totalCountProgress = SimpleDoubleProperty().apply { bind(downloadedCount * 1.0 / totalCount) }
}
