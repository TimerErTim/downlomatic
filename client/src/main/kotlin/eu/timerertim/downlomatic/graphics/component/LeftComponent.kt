package eu.timerertim.downlomatic.graphics.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
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
import eu.timerertim.downlomatic.api.APIPath
import eu.timerertim.downlomatic.api.APIRequest
import eu.timerertim.downlomatic.api.APIRequest.Companion.executeRequest
import eu.timerertim.downlomatic.api.APIState
import eu.timerertim.downlomatic.core.video.Video
import eu.timerertim.downlomatic.core.video.VideoItem
import eu.timerertim.downlomatic.graphics.component.util.*
import eu.timerertim.downlomatic.graphics.theme.icons
import eu.timerertim.downlomatic.graphics.theme.outline
import eu.timerertim.downlomatic.graphics.window.sdp
import eu.timerertim.downlomatic.graphics.window.ssp
import eu.timerertim.downlomatic.state.DownloadSelectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.Cursor

@Composable
fun DownlomaticLeftContent(selectionState: DownloadSelectionState) {
    Column(verticalArrangement = Arrangement.spacedBy(5.sdp), modifier = Modifier.padding(5.sdp)) {
        DownloadAllButton()
        HostSelection(selectionState)
        VideoSelection(selectionState.videosRequest)
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
fun HostSelection(selectionState: DownloadSelectionState) {
    val hostsRequest = selectionState.hostsRequest
    var host by selectionState::selectedHost
    val hostsRequestState = hostsRequest.state
    val hosts = if (hostsRequestState is APIState.Loaded) hostsRequestState.payload else null

    Row(horizontalArrangement = Arrangement.spacedBy(5.sdp)) {
        DropdownField(hosts,
            value = host,
            onValueChanged = {
                host = it
                val videosRequest = selectionState.videosRequest
                val videosRequestState = videosRequest?.state
                if (videosRequestState is APIState.Error) {
                    CoroutineScope(Dispatchers.IO).launch {
                        videosRequest.executeRequest(APIPath.ALL_VIDEOS_OF_HOST.HOST_ARGUMENT to it)
                    }
                }
            },
            onClicked = {
                if (hostsRequestState is APIState.Error) {
                    CoroutineScope(Dispatchers.IO).launch {
                        hostsRequest.executeRequest()
                    }
                }
            },
            prompt = {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Select host...",
                        color = MaterialTheme.colors.outline,
                        style = MaterialTheme.typography.body2
                    )
                    when (hostsRequestState) {
                        is APIState.Waiting ->
                            CircularProgressIndicator(strokeWidth = 1.sdp, modifier = Modifier.size(16.sdp))
                        is APIState.Error ->
                            Icon(
                                MaterialTheme.icons.ErrorOutline, "Error", tint = MaterialTheme.colors.error,
                                modifier = Modifier.size(16.sdp)
                            )
                        else -> {}
                    }
                }
            }, selectedRenderer = {
                Text(it, style = MaterialTheme.typography.body2)
            }, placeholder = {
                when (hostsRequestState) {
                    is APIState.Waiting -> CircularProgressIndicator(
                        strokeWidth = 2.sdp,
                        modifier = Modifier.size(24.sdp).align(Alignment.CenterHorizontally)
                    )
                    is APIState.Error -> Text(
                        hostsRequestState.exception.message ?: "Error occurred",
                        color = MaterialTheme.colors.error, style = MaterialTheme.typography.body1,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    else -> {}
                }
            }, modifier = Modifier.fillMaxWidth().weight(1F)
        )

        Button(onClick = {
            println("Single Host Download of $host")
        }, modifier = Modifier.size(28.sdp).align(Alignment.CenterVertically), contentPadding = PaddingValues(5.sdp)) {
            Icon(MaterialTheme.icons.Download, "Download", Modifier.size(20.sdp))
        }

    }
}

@Composable
fun VideoSelection(videosRequest: APIRequest<List<Video>, TreeNode<VideoItem>>?) {
    val videosRequestState = videosRequest?.state
    val (filter, setFilter) = remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(5.sdp)) {
        VideoSearchField(filter, setFilter)

        VideoTreeList(videosRequestState, filter)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun VideoSearchField(textValue: String, setTextValue: (String) -> Unit) {
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
fun VideoTreeList(state: APIState<TreeNode<VideoItem>>?, filter: String) {
    fun checkVideoItem(videoItem: VideoItem): Boolean {
        return videoItem.longDescription.contains(filter) || videoItem.videos.all {
            it.details.tags.any { tag ->
                tag.value.contains(filter)
            }
        }
    }

    Column(
        modifier = Modifier.border(1.sdp, MaterialTheme.colors.outline, MaterialTheme.shapes.small).fillMaxSize()
            .padding(5.sdp)
    ) {
        when (state) {
            null -> Text(
                "No host selected...",
                color = MaterialTheme.colors.outline, style = MaterialTheme.typography.body1,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            is APIState.Waiting -> CircularProgressIndicator(
                strokeWidth = 2.sdp,
                modifier = Modifier.size(24.sdp).align(Alignment.CenterHorizontally)
            )
            is APIState.Loaded -> TreeList(
                state.payload.filter(::checkVideoItem),
                modifier = Modifier.fillMaxSize()
            ) { value, expanded ->
                VideoTreeNode(value, expanded)
            }
            is APIState.Error -> Text(
                state.exception.message ?: "Error occurred",
                color = MaterialTheme.colors.error, style = MaterialTheme.typography.body1,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            else -> {}
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun VideoTreeNode(value: VideoItem, expanded: Boolean) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        TooltipArea(tooltip = {
            TooltipCard(value.longDescription)
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
                    value.shortDescription,
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
                    value.videos.forEach {
                        println("Started download of: ${it.url}")
                    }
                })
        )
    }
}
