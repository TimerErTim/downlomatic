package eu.timerertim.downlomatic.api

import eu.timerertim.downlomatic.core.format.VideoDetailsFormatter
import eu.timerertim.downlomatic.core.meta.VideoDetails
import eu.timerertim.downlomatic.core.video.Video
import eu.timerertim.downlomatic.core.video.VideoItem
import eu.timerertim.downlomatic.graphics.component.util.TreeNode
import kotlin.reflect.KProperty1

fun List<Video>.toTree(): TreeNode<VideoItem> =
    TreeNode(VideoItem(this, "", "")) { rootItem ->
        val videos = rootItem.videos

        val priorities = listOf(
            1 to setOf(VideoDetailsGroup(VideoDetails::series, "/S", "/S")),
            2 to setOf(VideoDetailsGroup(VideoDetails::season, "Season /s", "/S - S/s")),
            3 to setOf(
                VideoDetailsGroup(
                    VideoDetails::episode, longFormat = VideoDetailsFormatter.DEFAULT_ENTRY_FORMAT,
                    shortFormat = "/[E/e /N/]/[/!NEpisode /e/] (/L - /T)/[ (/y)/]"
                ),
                VideoDetailsGroup(
                    VideoDetails::title, longFormat = VideoDetailsFormatter.DEFAULT_ENTRY_FORMAT,
                    shortFormat = "/N (/L - /T)/[ (/y)/]"
                )
            )
        )

        val (children, leafs) = videos.group(priorities)
        this.children += children
        this.leafs += leafs
    }

data class VideoDetailsGroup(val key: KProperty1<VideoDetails, Any?>, val shortFormat: String, val longFormat: String)

private fun List<Video>.group(
    priorities: List<Pair<Int, Set<VideoDetailsGroup>>>
): Pair<List<TreeNode<VideoItem>>, List<VideoItem>> {
    val mutablePriorities = priorities.sortedBy { it.first }.toMutableList()
    val ungroupedList = this.toMutableList()
    val children = mutableListOf<TreeNode<VideoItem>>()
    val leafs = mutableListOf<VideoItem>()

    while (mutablePriorities.size > 1) {
        val currentGroups = mutablePriorities.removeFirst().second
        val nextGroups = mutablePriorities.first().second

        for (currentGroup in currentGroups) {
            val currentList = ungroupedList.filter { currentGroup.key.get(it.details) != null }
            ungroupedList.removeAll(currentList)

            val currentGroupedList = currentList.groupBy { currentGroup.key.get(it.details) }
            val videoItems = currentGroupedList.map {
                VideoItem(
                    it.value,
                    VideoDetailsFormatter(currentGroup.shortFormat).format(it.value.first().details),
                    VideoDetailsFormatter(currentGroup.longFormat).format(it.value.first().details)
                )
            }

            children += videoItems.map {
                TreeNode(it) { item ->
                    val (children, leafs) = item.videos.group(mutablePriorities)
                    this.children += children
                    this.leafs += leafs
                }
            }
        }
    }

    val lastGroups = mutablePriorities.single().second
    for (lastGroup in lastGroups) {
        leafs += ungroupedList.filter { lastGroup.key.get(it.details) != null }.map {
            VideoItem(
                it,
                VideoDetailsFormatter(lastGroup.shortFormat).format(it.details),
                VideoDetailsFormatter(lastGroup.longFormat).format(it.details)
            )
        }
    }

    return Pair(children, leafs)
}
