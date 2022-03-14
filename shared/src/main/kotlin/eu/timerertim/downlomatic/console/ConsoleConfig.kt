package eu.timerertim.downlomatic.console

/**
 * The ConsoleConfig contains information necessary for building a [Console] object.
 *
 * It allows different argument configurations for different implementations.
 */
data class ConsoleConfig(
    val arguments: Array<out Argument>,
    val argumentGroups: Array<out ArgumentGroup>,
    val usage: String? = null,
    val header: String = "",
    val footer: String = "",
    val executableName: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ConsoleConfig

        if (!arguments.contentEquals(other.arguments)) return false
        if (!argumentGroups.contentEquals(other.argumentGroups)) return false
        if (usage != other.usage) return false
        if (header != other.header) return false
        if (footer != other.footer) return false

        return true
    }

    override fun hashCode(): Int {
        var result = arguments.contentHashCode()
        result = 31 * result + argumentGroups.contentHashCode()
        result = 31 * result + (usage?.hashCode() ?: 0)
        result = 31 * result + header.hashCode()
        result = 31 * result + footer.hashCode()
        return result
    }
}
