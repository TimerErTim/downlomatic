package eu.timerertim.downlomatic.state

import androidx.compose.runtime.*
import eu.timerertim.downlomatic.api.APIPath
import eu.timerertim.downlomatic.api.APIRequest
import eu.timerertim.downlomatic.api.APIRequest.Companion.executeRequest
import eu.timerertim.downlomatic.api.toTree
import eu.timerertim.downlomatic.core.host.Host
import eu.timerertim.downlomatic.core.video.Video
import eu.timerertim.downlomatic.core.video.VideoItem
import eu.timerertim.downlomatic.graphics.component.util.TreeNode
import kotlinx.coroutines.*

class DownloadSelectionState {
    val nsfwState = mutableStateOf(false)
    var nsfw by nsfwState

    val selectedHostState = mutableStateOf<Host?>(null)
    var selectedHost by selectedHostState

    val allVideosRequest = APIRequest(APIPath.ALL_VIDEOS) { videos: List<Video> ->
        videos.filter { it.host.isNSFW || nsfw }
    }

    val hostsRequest by lazy {
        val request = APIRequest(APIPath.ALL_HOSTS) { it: List<Host> ->
            it.filter { host -> !host.isNSFW || nsfw }
        }
        CoroutineScope(Dispatchers.IO).launch {
            request.executeRequest()
        }
        request
    }

    private val videosMap = mutableStateMapOf<Host, APIRequest<List<Video>, TreeNode<VideoItem>>>()
    val videosRequest: APIRequest<List<Video>, TreeNode<VideoItem>>?
        get() {
            val host = selectedHost
            return if (host == null) null else videosMap.getOrPut(host) {
                val request = APIRequest(APIPath.ALL_VIDEOS_OF_HOST, List<Video>::toTree)
                CoroutineScope(Dispatchers.IO).launch {
                    request.executeRequest(APIPath.ALL_VIDEOS_OF_HOST.HOST_ARGUMENT to host.domain)
                }
                request
            }
        }
}
