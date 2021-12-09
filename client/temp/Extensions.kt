package eu.timerertim.downlomatic.graphics

import javafx.beans.property.SimpleLongProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.control.ProgressBar
import javafx.scene.layout.StackPane
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import org.fxmisc.easybind.EasyBind
import tornadofx.*
import kotlin.math.roundToInt

// Custom Controls
/**
 * This is a [ProgressBar] with a percentage text hovering over it.
 */
class ProgressBarIndicator : StackPane() {
    private val progressBar = progressbar {
        useMaxWidth = true
    }
    private val text = label(EasyBind.map(progressBar.progressProperty()) {
        val percentage = if (it.toDouble().isNaN()) 0 else (it.toDouble() * 1000).toInt() / 10.0
        "$percentage%"
    })

    var progress by progressBar::progress

    @JvmOverloads
    fun bind(property: ObservableValue<Number>, readonly: Boolean = false) = progressBar.bind(property, readonly)

    fun style(append: Boolean = false, op: InlineCss.() -> Unit) = progressBar.style(append, op)
}

fun EventTarget.progressbarindicator(initialValue: Double? = null, op: ProgressBarIndicator.() -> Unit = {}) =
    ProgressBarIndicator().attachTo(this, op) {
        if (initialValue != null) it.progress = initialValue
    }

fun EventTarget.progressbarindicator(property: ObservableValue<Number>, op: ProgressBarIndicator.() -> Unit = {}) =
    progressbarindicator().apply {
        bind(property)
        op(this)
    }

// Custom Properties
/**
 * This property calculates the change of the given [toObserve] property between the [delay] time given (which is in ms).
 * The property automatically notifies other properties it is bound to. The change is returned as "delta per second".
 */
class SimpleDeltaLongProperty @JvmOverloads constructor(
    private val toObserve: SimpleLongProperty,
    private val delay: Long = 1000
) : SimpleLongProperty() {
    private var value: Long = 0
    private var prevValue = toObserve.get()
    private val scope = CoroutineScope(Dispatchers.Unconfined)

    init {
        scope.launch { // Start the coroutine doing the calculation
            while (true) {
                val currentValue = toObserve.value
                value = (currentValue - prevValue) * 1000 / delay
                prevValue = currentValue
                launch(Dispatchers.JavaFx) { // Starts the coroutine notifying bound properties about the change
                    fireValueChangedEvent()
                }
                delay(delay)
            }
        }
    }

    override fun get() = value
}