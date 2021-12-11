package eu.timerertim.downlomatic.graphics.component.util

import androidx.compose.ui.awt.ComposeWindow
import java.awt.FileDialog
import java.io.File

fun openFileDialog(
    window: ComposeWindow? = null,
    title: String,
    allowedExtensions: List<String>,
    allowMultiSelection: Boolean = true
): Set<File> {
    return FileDialog(window, title, FileDialog.SAVE).apply {
        isMultipleMode = allowMultiSelection

        // windows
        file = allowedExtensions.joinToString(";") { "*$it" } // e.g. '*.jpg'

        // linux
        setFilenameFilter { _, name ->
            allowedExtensions.any {
                name.endsWith(it)
            }
        }

        isVisible = true
    }.files.toSet()
}
