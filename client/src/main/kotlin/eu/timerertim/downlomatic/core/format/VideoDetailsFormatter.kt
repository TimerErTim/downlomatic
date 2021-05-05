package eu.timerertim.downlomatic.core.format

import eu.timerertim.downlomatic.core.meta.VideoDetails
import java.io.File
import java.util.*
import java.util.function.Consumer
import java.util.regex.Pattern
import java.util.stream.Collectors
import kotlin.reflect.KProperty1

/**
 * Allows to format video titles comparable to [java.time.format.DateTimeFormatter].
 *
 *
 * The formatting happens
 * through [VideoDetailsFormatter.format] which takes a [Metadata] object as input. Everything you need to know is
 * described in that methods JavaDoc documentation. The format template is assigned in the [VideoDetailsFormatter] constructor.
 */
class VideoDetailsFormatter(private val pattern: String) {
    /**
     * Formats the episodes description to a readable `String` using the given expression.
     *
     *
     * ** Important to read the documentation under the following link:  [WIKI_URL]**
     *
     * @param expression a String expression used as template for formatting
     * @return a formatted String representation of an episode
     */
    fun format(videoDetails: VideoDetails): String {
        val identifiers = mutableListOf(*staticIdentifiers.toTypedArray()).also {
            for (template in templateIdentifiers) {
                it.add(template[videoDetails])
            }
        }.toList()

        var subSegments: List<String>
        var result: String = pattern

        // If the expression is a segment
        if (findSegments(result).also { subSegments = it }.size == 1 && subSegments[0] == result) {
            result = result.substring(2, result.length - 2)
            subSegments = findSegments(result)

            // Remove inner segments
            val replacements: MutableList<Literal> = LinkedList()
            for (s in subSegments) {
                replacements.add(Illegal(s))
            }
            val temp = replaceSimultaneous(result, replacements)

            // Return empty string according to segment logic
            for (identifier in identifiers) {
                val hasValue = identifier.hasValue()
                if (temp.contains(identifier.identifier) && !hasValue || temp.contains(identifier.negativeIdentifier) && hasValue) {
                    return ""
                }
            }
        }

        // Calculate replacements for inner segments
        val replacements: MutableList<Literal> = LinkedList()
        for (s in subSegments) {
            replacements.add(Literal(s, VideoDetailsFormatter(s).format(videoDetails)))
        }

        // Return filled in expression
        return replaceSimultaneous(result, replacements, identifiers)
    }

    private fun replaceSimultaneous(
        input: String,
        extra: List<Identifier>,
        baseIdentifiers: List<Identifier> = listOf()
    ): String {
        // Create replacement pool
        val identifiers: MutableList<Identifier> = LinkedList(extra)
        identifiers.addAll(baseIdentifiers)

        // Create replacement regular expression
        val regexp = identifiers.stream().map { obj: Identifier -> obj.replaceRegex }
            .collect(Collectors.joining("|"))
        return if (regexp.isEmpty()) {
            // Return input as is if there's nothing to replace
            input
        } else {
            // Create replacement map out of replacement pool
            val replacements: MutableMap<String, String> = HashMap()
            identifiers.forEach(Consumer { identifier: Identifier ->
                replacements[identifier.identifier] = identifier.getValue()
                replacements[identifier.negativeIdentifier] = ""
            })

            // Replace everything "simultaneously"
            val sb = StringBuffer()
            val p = Pattern.compile(regexp)
            val m = p.matcher(input)
            while (m.find()) {
                m.appendReplacement(sb, replacements[m.group()])
            }
            m.appendTail(sb)
            sb.toString()
        }
    }

    private open class Identifier(
        val identifier: String,
        val negativeIdentifier: String,
        private val value: String?
    ) {
        constructor(key: String, value: String?) : this("/$key", "/!$key",
            value?.replace(
                staticIdentifiers
                    .filterIsInstance<Illegal>()
                    .map { obj: Illegal -> obj.replaceRegex }
                    .joinToString("|").toRegex(),
                ""
            ))

        open fun hasValue(): Boolean {
            return value != null
        }

        open val replaceRegex: String?
            get() = "(" + Pattern.quote(identifier) + "|" +
                    Pattern.quote(negativeIdentifier) + ")"

        fun getValue(): String {
            return value ?: ""
        }
    }

    private open class Literal(key: String, value: String) :
        Identifier(key, Character.MIN_VALUE.toString(), value) {
        override fun hasValue(): Boolean {
            return true
        }

        override val replaceRegex: String?
            get() = "(" + Pattern.quote(identifier) + ")"
    }

    private class Illegal(key: String) : Literal(key, "")

    private class IdentifierTemplate<T>(
        val key: String,
        val value: KProperty1<VideoDetails, T>,
        val modifier: T.() -> Any? = { this }
    ) {
        operator fun get(details: VideoDetails) = Identifier(key, modifier(value.get(details))?.toString())
    }

    companion object {
        const val WIKI_URL = "https://github.com/TimerErTim/downlomatic/wiki/Formatting"

        private val staticIdentifiers = listOf<Identifier>(
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

        private val templateIdentifiers = listOf(
            IdentifierTemplate("N", VideoDetails::title),
            IdentifierTemplate("S", VideoDetails::series),
            IdentifierTemplate("s", VideoDetails::season),
            IdentifierTemplate("e", VideoDetails::episode),
            IdentifierTemplate("L", VideoDetails::audienceLanguage),
            IdentifierTemplate("D", VideoDetails::spokenLanguage),
            IdentifierTemplate("S", VideoDetails::subtitleLanguage),
            IdentifierTemplate("T", VideoDetails::translation),
            IdentifierTemplate("y", VideoDetails::release) {
                this?.year
            }
        )

        private fun findSegments(input: String): List<String> {
            val segments: MutableList<String> = LinkedList()
            val stack = Stack<String>()
            var startIndex = 0
            var endIndex: Int
            var openingIndex: Int
            var closingIndex: Int
            var index = 0
            // Checking for opening and closing brackets with a stack
            while (true) {
                openingIndex = input.indexOf("/[", index)
                closingIndex = input.indexOf("/]", index)
                if (openingIndex == -1 && closingIndex == -1) {
                    break
                } else if (openingIndex < closingIndex && openingIndex != -1) {
                    if (stack.isEmpty()) {
                        startIndex = openingIndex
                    }
                    stack.push("/[")
                    index = openingIndex
                } else if (closingIndex < openingIndex || openingIndex == -1) {
                    if (!stack.isEmpty()) {
                        stack.pop()
                        if (stack.isEmpty()) {
                            // If the stack is empty, the last bracket must have been reached, so we can save a segment
                            endIndex = closingIndex + 1
                            segments.add(input.substring(startIndex, endIndex + 1))
                        }
                    }
                    index = closingIndex
                }
                index++
            }
            return segments
        }
    }
}