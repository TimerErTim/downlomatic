package eu.timerertim.downlomatic.graphics

import javafx.scene.paint.Color
import javafx.scene.text.Font
import tornadofx.*

/**
 * The global CSS stylesheet which effects every possible GUI component.
 */
class Style : Stylesheet() {
    companion object {
        val robotoNormal by lazy { loadFont("/fonts/Roboto/Roboto-Regular.ttf", 12.0)!! }
        val robotoItalic by lazy { loadFont("/fonts/Roboto/Roboto-Italic.ttf", 12.0)!! }
        val robotoBlack by lazy { loadFont("/fonts/Roboto/Roboto-Black.ttf", 12.0)!! }
        val robotoBlackItalic by lazy { loadFont("/fonts/Roboto/Roboto-BlackItalic.ttf", 12.0)!! }
        val robotoBold by lazy { loadFont("/fonts/Roboto/Roboto-Bold.ttf", 12.0)!! }
        val robotoBoldItalic by lazy { loadFont("/fonts/Roboto/Roboto-BoldItalic.ttf", 12.0)!! }
        val robotoMedium by lazy { loadFont("/fonts/Roboto/Roboto-Medium.ttf", 12.0)!! }
        val robotoMediumItalic by lazy { loadFont("/fonts/Roboto/Roboto-MediumItalic.ttf", 12.0)!! }
        val robotoLight by lazy { loadFont("/fonts/Roboto/Roboto-Light.ttf", 12.0)!! }
        val robotoLightItalic by lazy { loadFont("/fonts/Roboto/Roboto-LightItalic.ttf", 12.0)!! }
        val robotoThin by lazy { loadFont("/fonts/Roboto/Roboto-Thin.ttf", 12.0)!! }
        val robotoThinItalic by lazy { loadFont("/fonts/Roboto/Roboto-ThinItalic.ttf", 12.0)!! }
    }

    init {
        root {
            fontFamily = robotoNormal.family
        }

        progressBar {
            bar {
                backgroundInsets += box(1.px)
                backgroundRadius += box(10.px)
            }
            track {
                borderRadius += box(10.px)
                backgroundRadius += box(10.px)
                backgroundColor += Color.LIGHTGRAY
            }
        }
    }
}