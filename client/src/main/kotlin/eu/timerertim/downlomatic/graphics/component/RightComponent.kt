package eu.timerertim.downlomatic.graphics.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import eu.timerertim.downlomatic.graphics.component.util.BasicOutlinedTextField
import eu.timerertim.downlomatic.graphics.window.sdp
import eu.timerertim.downlomatic.graphics.window.ssp
import javax.swing.JFileChooser

@Composable
fun DownlomaticRightContent() {
    Column(verticalArrangement = Arrangement.spacedBy(5.sdp), modifier = Modifier.padding(5.sdp).fillMaxHeight()) {
        FormatField()
        DestinationField()
    }
}

@Composable
fun ColumnScope.FormatField() {
    val (format, setFormat) = remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(1.sdp)) {
        Text("Video Format", style = MaterialTheme.typography.overline.copy(letterSpacing = 0.5.ssp))
        BasicOutlinedTextField(
            format, setFormat,
            singleLine = true, textStyle = MaterialTheme.typography.caption
        )
    }
}

@Composable
fun ColumnScope.DestinationField() {
    val (destination, setDestination) = remember { mutableStateOf("") }
    val (textFieldSize, setTextFieldSize) = remember { mutableStateOf(IntSize.Zero) }

    Column(verticalArrangement = Arrangement.spacedBy(1.sdp)) {
        Text("Target Directory", style = MaterialTheme.typography.overline.copy(letterSpacing = 0.5.ssp))
        Row(horizontalArrangement = Arrangement.spacedBy(5.sdp)) {
            BasicOutlinedTextField(
                destination, setDestination,
                singleLine = true, textStyle = MaterialTheme.typography.caption,
                modifier = Modifier.weight(1F).onSizeChanged(setTextFieldSize)
            )
            Button(
                onClick = {
                    //openFileDialog(title = "Destination", allowedExtensions = emptyList())
                    JFileChooser().showSaveDialog(null)
                },
                modifier = Modifier.size(textFieldSize.height.dp).align(Alignment.CenterVertically),
                contentPadding = PaddingValues(5.sdp)
            ) {
                Text("...", style = MaterialTheme.typography.caption)
            }
        }
    }
}
