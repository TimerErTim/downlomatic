package eu.timerertim.downlomatic.graphics.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import eu.timerertim.downlomatic.graphics.color.*
import eu.timerertim.downlomatic.graphics.window.sdp
import eu.timerertim.downlomatic.graphics.window.ssp

private val LightTheme = lightColors(
    primary = md_theme_light_primary,
    primaryVariant = md_theme_light_primaryContainer,
    onPrimary = md_theme_light_onPrimary,
    secondary = md_theme_light_secondary,
    secondaryVariant = md_theme_light_secondaryContainer,
    onSecondary = md_theme_light_onSecondary,
    error = md_theme_light_error,
    onError = md_theme_light_onError,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface
)

private val DarkTheme = darkColors(
    primary = md_theme_dark_primary,
    primaryVariant = md_theme_dark_primaryContainer,
    onPrimary = md_theme_dark_onPrimary,
    secondary = md_theme_dark_secondary,
    secondaryVariant = md_theme_dark_secondaryContainer,
    onSecondary = md_theme_dark_onSecondary,
    error = md_theme_dark_error,
    onError = md_theme_dark_onError,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface
)

private val ShapeTheme
    get() = Shapes(
        small = RoundedCornerShape(4.sdp),
        medium = RoundedCornerShape(4.sdp),
        large = RoundedCornerShape(0.sdp)
    )

private val FontTheme
    get() = Typography(
        h1 = TextStyle(
            fontWeight = FontWeight.Light,
            fontSize = 96.ssp,
            letterSpacing = (-1.5).ssp
        ),
        h2 = TextStyle(
            fontWeight = FontWeight.Light,
            fontSize = 60.ssp,
            letterSpacing = (-0.5).ssp
        ),
        h3 = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 48.ssp,
            letterSpacing = 0.ssp
        ),
        h4 = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 34.ssp,
            letterSpacing = 0.25.ssp
        ),
        h5 = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 24.ssp,
            letterSpacing = 0.ssp
        ),
        h6 = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 20.ssp,
            letterSpacing = 0.15.ssp
        ),
        subtitle1 = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 16.ssp,
            letterSpacing = 0.15.ssp
        ),
        subtitle2 = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 14.ssp,
            letterSpacing = 0.1.ssp
        ),
        body1 = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 16.ssp,
            letterSpacing = 0.5.ssp
        ),
        body2 = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 14.ssp,
            letterSpacing = 0.25.ssp
        ),
        button = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 14.ssp,
            letterSpacing = 1.25.ssp
        ),
        caption = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 12.ssp,
            letterSpacing = 0.4.ssp
        ),
        overline = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 10.ssp,
            letterSpacing = 1.5.ssp
        )
    )

private val ColorTheme
    @Composable
    get() = when (currentThemeMode) {
        ColorThemeMode.SYSTEM -> if (isSystemInDarkTheme()) DarkTheme else LightTheme
        ColorThemeMode.DARK -> DarkTheme
        ColorThemeMode.LIGHT -> LightTheme
    }

private val IconTheme = Icons.Default

@Composable
fun DownlomaticTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = ColorTheme,
        shapes = ShapeTheme,
        typography = FontTheme,
        content = content
    )
}

val MaterialTheme.icons get() = IconTheme
