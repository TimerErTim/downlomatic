package eu.timerertim.downlomatic.api

suspend fun mockRequest(apiPath: APIPath, vararg arguments: Pair<APIPathArgument, String>): Any {
    val argumentValues = arguments.toMap()
    return when (apiPath) {
        APIPath.ALL_HOSTS -> requestHosts()
        APIPath.ALL_VIDEOS_OF_HOST -> requestVideos(
            argumentValues[APIPath.ALL_VIDEOS_OF_HOST.HOST_ARGUMENT] ?: throw IllegalArgumentException()
        )
    }
}
