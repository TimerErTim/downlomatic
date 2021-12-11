package eu.timerertim.downlomatic.util.logging

/** This enum defines a set of logging levels that can be used to control logging output. The logging Level objects are
 * ordered. Enabling logging at a given level also enables logging at all higher levels.
 *
 * The levels in descending order are:
 * - [ASSERT] (highest value)
 * - [FATAL]
 * - [ERROR]
 * - [WARN]
 * - [INFO]
 * - [DEBUG] (lowest value)
 *
 * In addition there is a level [OFF] that can be used to turn off logging, and a level [ALL] that can be used to enable
 * logging of all messages.
 */
enum class Level(private val level: java.util.logging.Level) {
    /** Turns logging off */
    OFF(CustomLevel("OFF", Int.MAX_VALUE)),

    /** Describes cases that should never ever occur */
    ASSERT(CustomLevel("ASSERT", 1200)),

    /** Fatal error upon program execution can't continue anymore */
    FATAL(CustomLevel("FATAL", 1100)),

    /** A problem which needs attention now */
    ERROR(CustomLevel("ERROR", 1000)),

    /** An occurrence which may become an error in the future */
    WARN(CustomLevel("WARN", 900)),

    /** General information about program execution */
    INFO(CustomLevel("INFO", 800)),

    /** Information useful for debugging */
    DEBUG(CustomLevel("DEBUG", 700)),

    /** Turns all logging on */
    ALL(CustomLevel("ALL", Int.MIN_VALUE));

    companion object {
        private val levelMap = mapOf(*Array(values().size) {
            val level = values()[it]
            Pair(level.level, level)
        })

        /**
         * Returns the to [level] equivalent [Level] constant or the [default] value if there is no equivalent.
         */
        @JvmSynthetic
        fun Log._findLevel(level: java.util.logging.Level, default: Level) = levelMap.getOrDefault(level, default)
    }

    val Log._level
        @JvmSynthetic
        get() = level

    private class CustomLevel(name: String, value: Int) : java.util.logging.Level(name, value)
}