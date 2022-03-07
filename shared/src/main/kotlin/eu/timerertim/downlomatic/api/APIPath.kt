package eu.timerertim.downlomatic.api

sealed class APIPath(vararg path: CharSequence) {
    object ALL_HOSTS : APIPath("/hosts/all")
    object ALL_VIDEOS_OF_HOST : APIPath("/videos/", APIPathArgument("host"), "/all") {
        val HOST_ARGUMENT = arguments[0]
    }

    object ALL_VIDEOS : APIPath("/videos/all")

    private val rawPath = path
    val path: String
    val arguments: List<APIPathArgument>

    init {
        var pathBuilder = ""
        val arguments = mutableListOf<APIPathArgument>()

        for (part in path) {
            pathBuilder += part
            if (part is APIPathArgument) {
                arguments += part
            }
        }

        this.path = pathBuilder
        this.arguments = arguments
    }

    fun query(vararg argumentValues: Pair<APIPathArgument, String>): String {
        val argumentMap = argumentValues.toMap()
        var pathBuilder = ""

        for (part in rawPath) {
            pathBuilder += if (part is APIPathArgument) argumentMap.getOrElse(part, part::getDefault) else part
        }

        return pathBuilder
    }
}
