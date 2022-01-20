package eu.timerertim.downlomatic.graphics.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import eu.timerertim.downlomatic.graphics.component.util.*
import eu.timerertim.downlomatic.graphics.theme.icons
import eu.timerertim.downlomatic.graphics.theme.outline
import eu.timerertim.downlomatic.graphics.window.sdp
import eu.timerertim.downlomatic.graphics.window.ssp
import java.awt.Cursor

@Composable
fun DownlomaticLeftContent() {
    Column(verticalArrangement = Arrangement.spacedBy(5.sdp), modifier = Modifier.padding(5.sdp)) {
        DownloadAllButton()
        HostSelection()
        VideoSelection()
    }
}

@Composable
fun DownloadAllButton() {
    Button(onClick = {
        println("Started Download of all hosts")
    }, modifier = Modifier.fillMaxWidth().height(30.sdp), contentPadding = PaddingValues(5.sdp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(7.5.sdp)) {
            Icon(
                MaterialTheme.icons.Download, "Download",
                modifier = Modifier.size(20.sdp).align(Alignment.CenterVertically)
            )
            Text("All Hosts", fontSize = 15.ssp)
        }
    }
}

@Composable
fun HostSelection() {
    var selectedEntry: String? by remember { mutableStateOf(null) }

    Row(horizontalArrangement = Arrangement.spacedBy(5.sdp)) {
        DropdownField(listOf("Hentaigasm.com", "asdasd"),
            selectedEntry,
            onValueChanged = {
                selectedEntry = it
            }, prompt = {
                Text("Select host...", color = MaterialTheme.colors.outline, style = MaterialTheme.typography.body2)
            }, selectedRenderer = {
                Text(it, style = MaterialTheme.typography.body2)
            }, modifier = Modifier.fillMaxWidth().weight(1F)
        )

        Button(onClick = {
            println("Single Host Download of $selectedEntry")
        }, modifier = Modifier.size(28.sdp).align(Alignment.CenterVertically), contentPadding = PaddingValues(5.sdp)) {
            Icon(MaterialTheme.icons.Download, "Download", Modifier.size(20.sdp))
        }

    }
}

@Composable
fun VideoSelection() {
    var rootNode by remember {
        mutableStateOf(TreeNode("").apply {
            repeat(10) {
                children += TreeNode("Child $it").apply {
                    repeat(5) {
                        children += TreeNode("Deep Child $it").apply {
                            repeat(2) {
                                leafs += "Deep Leaf $it"
                            }
                            children += TreeNode("Hell No").apply {
                                children += TreeNode("Another Hell No").apply {
                                    leafs += "This is it"
                                }
                            }
                        }
                    }
                    leafs += "Child Leaf"
                }
            }
            repeat(5) {
                leafs += "Leaf $it"
            }
            leafs += "Leaf with a very long text to simulate the name of a very long video/entry"
        })
    }
    var filter = remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(5.sdp)) {
        VideoSearchField(filter)

        VideoTreeList(rootNode.filter { it.contains(filter.value) })
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun VideoSearchField(state: MutableState<String>) {
    val (textValue, setTextValue) = state

    val focusManager = LocalFocusManager.current
    var hasFocus by remember { mutableStateOf(false) }

    BasicOutlinedTextField(
        textValue,
        setTextValue,
        placeholder = {
            Text("Search videos...", style = MaterialTheme.typography.caption, color = MaterialTheme.colors.outline)
        }, leadingIcon = {
            Icon(MaterialTheme.icons.Search, "Search", modifier = Modifier.size(18.sdp))
        }, trailingIcon = if (textValue.isNotEmpty()) {
            {
                Icon(
                    MaterialTheme.icons.Clear, "Clear", tint = MaterialTheme.colors.outline,
                    modifier = Modifier.size(18.sdp).clip(CircleShape).clickable {
                        setTextValue("")
                        if (hasFocus) focusManager.clearFocus()
                    }.pointerHoverIcon(PointerIcon(Cursor.getDefaultCursor()))
                )
            }
        } else null,
        modifier = Modifier.fillMaxWidth().onFocusChanged {
            hasFocus = it.hasFocus
        },
        textStyle = MaterialTheme.typography.caption,
        singleLine = true
    )
}

@Composable
fun VideoTreeList(state: TreeNode<String>) {
    ScrollableColumn(
        modifier = Modifier.border(1.sdp, MaterialTheme.colors.outline, MaterialTheme.shapes.small).fillMaxHeight()
            .padding(5.sdp)
    ) {
        Box(Modifier.padding(end = 8.sdp)) {
            TreeList(state, modifier = Modifier.fillMaxSize()) { value, expanded ->
                VideoTreeNode(value, expanded)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun VideoTreeNode(value: String, expanded: Boolean) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        TooltipArea(tooltip = {
            TooltipCard(value)
        }, modifier = Modifier.weight(1F)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.sdp)
            ) {

                Icon(
                    if (expanded) MaterialTheme.icons.VideoLibrary else MaterialTheme.icons.SmartDisplay,
                    "Video",
                    modifier = Modifier.size(15.sdp).align(Alignment.CenterVertically)
                )
                Text(
                    value,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.body2
                )
            }
        }
        Icon(
            MaterialTheme.icons.Download,
            "Download",
            modifier = Modifier.size(15.sdp).align(Alignment.CenterVertically).clip(CircleShape)
                .clickable(onClick = {
                    println("$value was clicked")
                })
        )
    }
}
