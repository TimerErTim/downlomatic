package eu.timerertim.downlomatic.graphics.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import eu.timerertim.downlomatic.graphics.component.util.BasicOutlinedTextField
import eu.timerertim.downlomatic.graphics.theme.outline
import eu.timerertim.downlomatic.graphics.window.sdp
import eu.timerertim.downlomatic.graphics.window.ssp
import eu.timerertim.downlomatic.state.DownloadConfigurationState
import javax.swing.JFileChooser

@Composable
fun DownlomaticRightContent(configurationState: DownloadConfigurationState) {
    Column(verticalArrangement = Arrangement.spacedBy(5.sdp), modifier = Modifier.padding(5.sdp).fillMaxHeight()) {
        FormatField(configurationState.videoFormatState, configurationState.defaultVideoFormat)
        DestinationField(configurationState.destinationDirectoryState, configurationState.defaultDestinationDirectory)
    }
}

@Composable
fun ColumnScope.FormatField(videoFormatState: MutableState<String?>, defaultVideoFormat: String) {
    val (format, setFormat) = videoFormatState

    Column(verticalArrangement = Arrangement.spacedBy(1.sdp)) {
        Text("Video Format", style = MaterialTheme.typography.overline.copy(letterSpacing = 0.5.ssp))
        BasicOutlinedTextField(
            format, setFormat,
            singleLine = true, textStyle = MaterialTheme.typography.caption,
            placeholder = {
                Text(
                    defaultVideoFormat,
                    style = MaterialTheme.typography.caption, color = MaterialTheme.colors.outline,
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
        )
    }
}

@Composable
fun ColumnScope.DestinationField(destinationDirectory: MutableState<String?>, defaultDestinationDirectory: String) {
    val (destination, setDestination) = destinationDirectory
    val (textFieldSize, setTextFieldSize) = remember { mutableStateOf(IntSize.Zero) }

    Column(verticalArrangement = Arrangement.spacedBy(1.sdp)) {
        Text("Target Directory", style = MaterialTheme.typography.overline.copy(letterSpacing = 0.5.ssp))
        Row(horizontalArrangement = Arrangement.spacedBy(5.sdp)) {
            BasicOutlinedTextField(
                destination, setDestination,
                singleLine = true, textStyle = MaterialTheme.typography.caption,
                modifier = Modifier.weight(1F).onSizeChanged(setTextFieldSize),
                placeholder = {
                    Text(
                        defaultDestinationDirectory,
                        style = MaterialTheme.typography.caption.copy(color = MaterialTheme.colors.outline),
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }
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
