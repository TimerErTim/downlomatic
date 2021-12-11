package eu.timerertim.downlomatic.graphics.theme

import androidx.compose.material.Colors
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import eu.timerertim.downlomatic.graphics.color.*

enum class ColorThemeMode {
    SYSTEM, LIGHT, DARK
}

var currentThemeMode by mutableStateOf(ColorThemeMode.SYSTEM)

val Colors.outline get() = if (isLight) md_theme_light_outline else md_theme_dark_outline
val Colors.onPrimaryVariant get() = if (isLight) md_theme_light_onPrimaryContainer else md_theme_dark_onPrimaryContainer
val Colors.onSecondaryVariant
    get() = if (isLight) md_theme_light_onSecondaryContainer else md_theme_dark_onSecondaryContainer
val Colors.tertiary get() = if (isLight) md_theme_light_tertiary else md_theme_dark_tertiary
val Colors.onTertiary get() = if (isLight) md_theme_light_onTertiary else md_theme_dark_onTertiary
val Colors.tertiaryVariant get() = if (isLight) md_theme_light_tertiaryContainer else md_theme_dark_tertiaryContainer
val Colors.onTertiaryVariant
    get() = if (isLight) md_theme_light_onTertiaryContainer else md_theme_dark_onTertiaryContainer
val Colors.errorVariant get() = if (isLight) md_theme_light_errorContainer else md_theme_dark_errorContainer
val Colors.onErrorVariant get() = if (isLight) md_theme_light_onErrorContainer else md_theme_dark_onErrorContainer
val Colors.surfaceVariant get() = if (isLight) md_theme_light_surfaceVariant else md_theme_dark_surfaceVariant
val Colors.onSurfaceVariant get() = if (isLight) md_theme_light_onSurfaceVariant else md_theme_dark_onSurfaceVariant
val Colors.inverseSurface get() = if (isLight) md_theme_light_inverseSurface else md_theme_dark_inverseSurface
val Colors.inverseOnSurface get() = if (isLight) md_theme_light_inverseOnSurface else md_theme_dark_inverseOnSurface