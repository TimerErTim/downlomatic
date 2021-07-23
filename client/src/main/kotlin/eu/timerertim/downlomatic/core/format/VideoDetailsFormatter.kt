package eu.timerertim.downlomatic.core.format

import eu.timerertim.downlomatic.core.meta.VideoDetails
import java.io.File
import java.util.*
import java.util.regex.Pattern
import java.util.stream.Collectors
import kotlin.reflect.KProperty1

/**
 * Allows to format video titles comparable to [java.time.format.DateTimeFormatter].
 *
 *
 * The formatting happens
 * through [VideoDetailsFormatter.format] which takes a [Metadata] object as input. Everything you need to know is
 * described in that methods JavaDoc documentation. The format template is assigned in the [VideoDetailsFormatter]
 * constructor.
 */
class VideoDetailsFormatter(pattern: String) {
    private val contentList = pattern.splitAsSegment(0, 0)
    private lateinit var identifiers: List<Replaceable>

    /**
     * Formats the [videoDetails] to a readable [String].
     *
     *
     * ** Important to read the documentation under the following link:  [WIKI_URL]**
     *
     * @param  videoDetails the `VideoDetails` to be formatted
     * @return a formatted String representation of a video
     */
    fun format(videoDetails: VideoDetails): String {
        identifiers = mutableListOf(*staticIdentifiers.toTypedArray()).apply {
            dynamicIdentifiers.forEach {
                add(it[videoDetails]) // Generate Identifiers from dynamic identifiers
            }
        }.sortedBy(Replaceable::priority) // Sort by priority

        return contentList.joinToString("") { it.process() }
    }

    companion object {
        const val WIKI_URL = "https://github.com/TimerErTim/downlomatic/wiki/Formatting"

        private const val openingBracket = "/["
        private const val closingBracket = "/]"

        // Replaceable which can be used for every format function call
        private val staticIdentifiers = listOf(
            Literal("//", File.separator),

            Illegal("/"),
            Illegal("\\"),
            Illegal("["),
            Illegal("]"),
            Illegal(":"),
            Illegal("*"),
            Illegal("?"),
            Illegal("\""),
            Illegal("<"),
            Illegal(">"),
            Illegal("|")
        )

        // Identifiers which need to be reevaluated on each format function call
        private val dynamicIdentifiers = listOf(
            IdentifierTemplate("N", VideoDetails::title),
            IdentifierTemplate("S", VideoDetails::series),
            IdentifierTemplate("s", VideoDetails::season),
            IdentifierTemplate("e", VideoDetails::episode),
            IdentifierTemplate("L", VideoDetails::audienceLanguage),
            IdentifierTemplate("A", VideoDetails::spokenLanguage),
            IdentifierTemplate("V", VideoDetails::subtitleLanguage),
            IdentifierTemplate("T", VideoDetails::translation),
            IdentifierTemplate("y", VideoDetails::release) {
                year
            }
        )

    }

    private sealed interface Processable {
        /**
         * Process this object to return a human readable [String].
         */
        fun process(): String
    }

    private inner class PatternPart(private val content: String) : Processable {
        override fun process() = content.replaceIdentifiers()

        fun checkIdentifiers() = content.checkForIdentifiers()

        override fun toString() = content
    }

    private inner class Segment(private val content: String) : Processable {
        private val contentList: List<Processable> = content.splitAsSegment()

        override fun process(): String {
            val patternPartContentList = contentList.filterIsInstance<PatternPart>()

            // Return empty string according to segment logic
            if (patternPartContentList.any { it.checkIdentifiers() }) {
                return ""
            }

            return contentList.joinToString("") { it.process() }
        }

        override fun toString() = content

        fun String.splitByContent() = split(content, ignoreCase = false, limit = 2)
    }

    private sealed class Replaceable(open val priority: Int) {
        open val replaceRegex get() = Regex("(${Regex.escape(key)})")
        abstract val key: String
        protected abstract val _value: String?

        val hasValue get() = _value != null
        val value get() = _value ?: ""
    }

    private class Identifier(
        override val key: String,
        value: String?
    ) : Replaceable(0) {
        val identifier = "/$key"
        val negativeIdentifier = "/!$key"
        val positiveIdentifier = "/?$key"

        override val replaceRegex = Regex(
            "(" + Pattern.quote(identifier) + "|" +
                    Pattern.quote(negativeIdentifier) + "|" +
                    Pattern.quote(positiveIdentifier) + ")"
        )

        override val _value = value?.replace(
            staticIdentifiers
                .filterIsInstance<Illegal>().joinToString("|") { it.replaceRegex.pattern }.toRegex(),
            ""
        ) // Remove Illegals from Identifiers, because it makes the most sense here
    }

    private class Literal(override val key: String, override val _value: String) : Replaceable(1)

    private class Illegal(override val key: String) : Replaceable(2) {
        override val _value: String? = null
    }

    private class IdentifierTemplate<T : Any>(
        val key: String,
        val value: KProperty1<VideoDetails, T?>,
        val modifier: T.() -> Any = { this }
    ) {
        operator fun get(details: VideoDetails) = Identifier(key, value.get(details)?.let { modifier(it).toString() })
    }

    private fun String.splitAsSegment(
        startMargin: Int = openingBracket.length,
        endMargin: Int = closingBracket.length
    ): List<Processable> {
        val mutableContentList = mutableListOf<Processable>()
        val trimmedContent = this.substring(startMargin, this.length - endMargin)
        val subSegments = trimmedContent.extractSegments().map {
            Segment(it)
        }

        // Build complex content list
        var trimmedContentSplit = listOf(trimmedContent)
        for (subSegment in subSegments) {
            trimmedContentSplit = with(subSegment) { trimmedContentSplit.last().splitByContent() }
            trimmedContentSplit.first().takeIf { it.isNotEmpty() }?.let { mutableContentList.add(PatternPart(it)) }
            mutableContentList.add(subSegment)
        }
        trimmedContentSplit.last().takeIf { it.isNotEmpty() }?.let { mutableContentList.add(PatternPart(it)) }

        return mutableContentList.toList()
    }

    private fun String.extractSegments(): List<String> {
        val segments: MutableList<String> = LinkedList()
        val stack = Stack<String>()
        var startIndex = 0
        var endIndex: Int
        var openingIndex: Int
        var closingIndex: Int
        var index = 0
        // Checking for opening and closing brackets with a stack
        while (true) {
            openingIndex = this.indexOf(openingBracket, index)
            closingIndex = this.indexOf(closingBracket, index)
            if (openingIndex == -1 && closingIndex == -1) {
                break
            } else if (openingIndex < closingIndex && openingIndex != -1) {
                if (stack.isEmpty()) {
                    startIndex = openingIndex
                }
                stack.push(openingBracket)
                index = openingIndex
            } else if (closingIndex < openingIndex || openingIndex == -1) {
                if (!stack.isEmpty()) {
                    stack.pop()
                    if (stack.isEmpty()) {
                        // If the stack is empty, the last bracket must have been reached, so we can save a segment
                        endIndex = closingIndex + 1
                        segments.add(this.substring(startIndex, endIndex + 1))
                    }
                }
                index = closingIndex
            }
            index++
        }
        return segments
    }

    private fun String.replaceIdentifiers(): String {
        // Create replacement regular expression
        val regexp = identifiers.stream().map { it.replaceRegex.pattern }
            .collect(Collectors.joining("|"))

        // Create replacement map out of replacement pool
        val replacements: MutableMap<String, String> = HashMap()
        identifiers.forEach {
            when (it) {
                is Identifier -> {
                    replacements[it.identifier] = it.value
                    replacements[it.negativeIdentifier] = ""
                    replacements[it.positiveIdentifier] = ""
                }
                else -> replacements[it.key] = it.value
            }
        }

        // Replace everything "simultaneously"
        val sb = StringBuffer()
        val p = Pattern.compile(regexp)
        val m = p.matcher(this)
        while (m.find()) {
            m.appendReplacement(sb, replacements[m.group()])
        }
        m.appendTail(sb)
        return sb.toString()
    }

    /**
     * Checks for validity regarding identifiers in a segment.
     * Returns true when [this] is invalid and should thus result in an empty segment.
     */
    private fun String.checkForIdentifiers(): Boolean {
        // Create replacement regular expression
        val regexp = identifiers.stream().map { it.replaceRegex.pattern }
            .collect(Collectors.joining("|"))

        // Shadow identifiers with a filtered list
        val identifiers = identifiers.filterIsInstance<Identifier>()

        // Create replacement map out of replacement pool
        val identifierMap: MutableMap<String, Identifier> = HashMap()
        val negativeIdentifierMap: MutableMap<String, Identifier> = HashMap()
        val positiveIdentifierMap: MutableMap<String, Identifier> = HashMap()
        identifiers.forEach {
            identifierMap[it.identifier] = it
            negativeIdentifierMap[it.negativeIdentifier] = it
            positiveIdentifierMap[it.positiveIdentifier] = it
        }

        // Check everything "simultaneously"
        val p = Pattern.compile(regexp)
        val m = p.matcher(this)
        while (m.find()) {
            if (identifierMap[m.group()]?.hasValue == false ||
                negativeIdentifierMap[m.group()]?.hasValue == true ||
                positiveIdentifierMap[m.group()]?.hasValue == false
            ) {
                return true
            }
        }
        return false
    }
}