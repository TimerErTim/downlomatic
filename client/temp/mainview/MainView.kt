package eu.timerertim.downlomatic.graphics.mainview

import com.sun.javafx.collections.ObservableListWrapper
import eu.timerertim.downlomatic.graphics.GraphicUtils.defaultSize
import eu.timerertim.downlomatic.graphics.progressbarindicator
import eu.timerertim.downlomatic.utils.Utils
import eu.timerertim.downlomatic.utils.Utils.toHumanReadableBytesPowerOfTen
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.control.Alert.AlertType
import javafx.scene.image.Image
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.controlsfx.glyphfont.FontAwesome
import org.fxmisc.easybind.EasyBind
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid
import org.kordamp.ikonli.javafx.FontIcon
import tornadofx.*
import kotlin.math.roundToInt

val controller = find<MainViewController>()

internal class TopMenu : View() {
    override val root = menubar {
        menu("File") {
            item("Exit", "Shortcut+Q").action(Platform::exit)
        }
    }
}

internal class TopView : View() {
    override val root = hbox {
        paddingAll = 10
        paddingBottom = 5
        spacing = 10.0


    }
}

internal class LeftView : View() {
    override val root = vbox {
        paddingAll = 5
        paddingLeft = 10
        spacing = 10.0

        val combo = combobox<String> {
            minWidth = 0.0
            isFillWidth = true
            maxWidth = Double.POSITIVE_INFINITY
            value = "Select Host"
            isFocusTraversable = false

            tooltip("The host you want to download from")

            longpress {
                alert(AlertType.INFORMATION, "Long pressed")
            }
        }
        val treeview = treetableview<String> {
            vgrow = Priority.ALWAYS
            useMaxHeight = true
            isFocusTraversable = false

            placeholder = label("Available Videos")
        }
    }
}

internal class CenterView : View() {
    override val root = vbox {
        paddingAll = 5.0
        spacing = 10.0

        listview<String>(ObservableListWrapper(mutableListOf())) {
            placeholder = label("Current Downloads")
            isFocusTraversable = false
            items += listOf("Stuff", "more", "amazing", "a", "b", "c", "d", "a", "b")

            cellFormat {
                graphic = cache {
                    hbox {
                        spacing = 5.0

                        val button = button("|||") {
                            isFocusTraversable = false
                        }
                        vbox {
                            hgrow = Priority.ALWAYS
                            spacing = 2.5

                            val label = label(it)
                            progressbar {
                                useMaxWidth = true
                                isFocusTraversable = false
                                maxHeightProperty().bind(button.heightProperty() - label.heightProperty() - this@vbox.spacing)
                            }
                        }
                        button {
                            graphic = FontIcon(FontAwesomeRegular.TIMES_CIRCLE).apply {
                                iconSizeProperty().bind(button.heightProperty())
                                iconColor = Color.RED
                            }
                            isFocusTraversable = false

                            action {
                                items.remove(it)
                                refresh()
                                println(items.joinToString())
                                println(it)
                                println()
                            }
                            style(append = true) {
                                borderColor += box(Color.GRAY)
                                padding = box(1.px)
                            }
                        }
                    }
                }
                style {
                    borderWidth += box(0.px, 0.px, 1.px, 0.px)
                    borderColor += box(Color.LIGHTGRAY)
                }
            }
        }
        listview<String> {
            isFocusTraversable = false
            vgrow = Priority.ALWAYS
            placeholder = label("Queued Downloads")
        }
    }
}

internal class RightView : View() {
    override val root = vbox {
        paddingAll = 5
        paddingRight = 10
        spacing = 10.0

        button("Stuff")
    }
}

internal class BottomView : View() {
    override val root = gridpane {
        useMaxWidth = true
        paddingAll = 10
        paddingTop = 5
        vgap = 5.0
        hgap = 5.0
        constraintsForColumn(1).hgrow = Priority.ALWAYS

        row {
            hbox {
                label("Progress by Bytes: ")
                label(EasyBind.map(controller.downloadedDeltaBytes) { it.toHumanReadableBytesPowerOfTen() + "/s" })
            }
            progressbarindicator(controller.totalByteProgress)
            hbox {
                alignment = Pos.CENTER_RIGHT

                label(EasyBind.map(controller.downloadedBytes) { it.toHumanReadableBytesPowerOfTen() })
                label("/")
                label(EasyBind.map(controller.totalBytes) { it.toHumanReadableBytesPowerOfTen() })
            }
        }
        row {
            label("Progress by finished Downloads:")
            progressbarindicator(controller.totalCountProgress)
            hbox {
                alignment = Pos.CENTER_RIGHT

                label(controller.downloadedCount)
                label("/")
                label(controller.totalCount)
            }
        }
    }
}

/**
 * This class represents the main view for the GUI.
 */
class MainView : View("Downlomatic") {
    init {
        importStylesheet(MainViewStyle::class)
    }

    override val root = borderpane {
        primaryStage.defaultSize()

        top {
            vbox {
                add<TopMenu>()
                add<TopView>()
            }
        }
        left<LeftView>()
        center<CenterView>()
        right<RightView>()
        bottom<BottomView>()
    }
}

