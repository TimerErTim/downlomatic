package eu.timerertim.downlomatic.core.video

data class VideoItem(val videos: List<Video>, val shortDescription: String, val longDescription: String) {
    constructor(video: Video, shortDescription: String, longDescription: String) : this(
        listOf(video),
        shortDescription,
        longDescription
    )
}
