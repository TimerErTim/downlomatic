package eu.timerertim.downlomatic.utils.logging

import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.logging.*

/**
 * Global logging object. Every logging action should happen through this.
 */
object Log {
    private val DEFAULT_CONSOLE_VERBOSITY = Level.ERROR
    private val DEFAULT_FILE_VERBOSITY = Level.ALL
    private const val FILE_SIZE = 128 * 1000L
    private const val FILE_COUNT = 2
    private const val FILE_APPEND = true
    private val DEFAULT_FILE_PATTERN = this::class.java.protectionDomain.codeSource.location.path
        .replace(File.separator, "/").replaceAfterLast("/lib/downlomatic", "", "")
        .replaceAfterLast("/lib", "").replaceAfterLast("/", "") +
            "logs/downlomatic%g.%u.log"

    private var fileHandler: FileHandler? = null
    private val consoleHandler = ConsoleHandler()
    private val logger = Logger.getGlobal()

    init {
        consoleHandler.level = DEFAULT_CONSOLE_VERBOSITY.level
        consoleHandler.formatter = MessageFormat

        logger.level = java.util.logging.Level.ALL
        logger.useParentHandlers = false
        logger.addHandler(consoleHandler)
    }

    @JvmStatic
    var consoleVerbosity: Level
        get() = Level.findLevel(consoleHandler.level)
        set(value) {
            consoleHandler.level = value.level

            // Enables/Disables detail format for debugging and lower log levels
            if (value.level.intValue() <= Level.DEBUG.level.intValue()) {
                consoleHandler.formatter = DetailFormat
            } else {
                consoleHandler.formatter = MessageFormat
            }
        }

    @JvmStatic
    var fileVerbosity = DEFAULT_FILE_VERBOSITY
        set(value) {
            field = value
            if (fileHandler != null) {
                fileHandler?.level = value.level
            }
        }


    @JvmStatic
    var fileLogging: Boolean
        get() = logger.handlers.contains(fileHandler)
        set(value) {
            if (fileHandler == null && value) {
                File(DEFAULT_FILE_PATTERN.replace("/", File.separator)).mkdirs()
                fileHandler = FileHandler(DEFAULT_FILE_PATTERN, FILE_SIZE, FILE_COUNT, FILE_APPEND)
                fileHandler?.level = fileVerbosity.level
                fileHandler?.formatter = DetailFormat
                logger.addHandler(fileHandler)
            } else if (fileHandler != null && !value) {
                logger.removeHandler(fileHandler)
                fileHandler?.close()
                fileHandler = null
            }
        }

    /**
     * Send a [DEBUG][Level.DEBUG] log message and log the optional [cause].
     */
    @JvmStatic
    @JvmOverloads
    fun d(message: String, cause: Throwable? = null) {
        logger.log(createLogEntry(Level.DEBUG, message, cause))
    }

    /**
     * Send a [INFO][Level.INFO] log message and log the optional [cause].
     */
    @JvmStatic
    @JvmOverloads
    fun i(message: String, cause: Throwable? = null) {
        logger.log(createLogEntry(Level.INFO, message, cause))
    }

    /**
     * Send a [WARN][Level.WARN] log message and log the optional [cause].
     */
    @JvmStatic
    @JvmOverloads
    fun w(message: String, cause: Throwable? = null) {
        logger.log(createLogEntry(Level.WARN, message, cause))
    }

    /**
     * Send a [ERROR][Level.ERROR] log message and log the optional [cause].
     */
    @JvmStatic
    @JvmOverloads
    fun e(message: String, cause: Throwable? = null) {
        logger.log(createLogEntry(Level.ERROR, message, cause))
    }

    /**
     * Send a [FATAL][Level.FATAL] log message and log the optional [cause].
     */
    @JvmStatic
    @JvmOverloads
    fun f(message: String, cause: Throwable? = null) {
        logger.log(createLogEntry(Level.FATAL, message, cause))
    }

    /**
     * What a Terrible Failure: Report a condition or exception that should never happen.
     */
    @JvmStatic
    @JvmOverloads
    fun wtf(message: String, cause: Throwable? = null) {
        logger.log(createLogEntry(Level.ASSERT, message, cause))
    }

    private fun createLogEntry(level: Level, message: String, cause: Throwable?): LogRecord {
        val entry = LogRecord(level.level, message)
        entry.thrown = cause
        entry.loggerName = javaClass.canonicalName

        // Get and set the stack
        val stack = CallerFinder.get()
        if (stack != null) {
            entry.sourceClassName = stack.className.removePrefix("eu.timerertim.downlomatic.")
            entry.sourceMethodName = stack.methodName
        }

        return entry
    }

    private object MessageFormat : Formatter() {
        override fun format(record: LogRecord): String {
            val message = formatMessage(record)

            val formatBuilder = StringBuilder(message)

            if (record.thrown != null) {
                formatBuilder.append("\n\tCause: ${record.thrown.localizedMessage}")
            }


            return formatBuilder.appendLine().toString()
        }
    }

    private object DetailFormat : Formatter() {
        override fun format(record: LogRecord): String {
            val zdt = ZonedDateTime.ofInstant(record.instant, ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("YYYY.MM.dd HH:mm:ss.SS"))
            var source = "${record.threadID}/"
            if (record.sourceClassName != null) {
                source += record.sourceClassName
                if (record.sourceMethodName != null) {
                    source += "::${record.sourceMethodName}"
                }
            } else {
                source += record.loggerName
            }

            val message = formatMessage(record)
            val level = record.level.localizedName

            val formatBuilder = StringBuilder("$zdt - $source [$level]: $message")

            if (record.thrown != null) {
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                pw.println()
                record.thrown.printStackTrace(pw)
                pw.close()
                formatBuilder.append(sw.toString().trimEnd().prependIndent("\t"))
            }


            return formatBuilder.appendLine().toString()
        }
    }

    private object CallerFinder {
        private val walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
        fun get(): StackWalker.StackFrame? = walker.walk {
            var lookingForLogger = true
            it.filter {
                val cname: String = it.className
                if (lookingForLogger) {
                    lookingForLogger = !isLoggerImplFrame(cname)
                    false
                } else (!isLoggerImplFrame(cname)).also { lookingForLogger = it }
            }.findFirst()
        }.orElse(null)

        private fun isLoggerImplFrame(cname: String) = cname == Log::class.qualifiedName

    }

    private fun Level.Companion.findLevel(level: java.util.logging.Level, default: Level = Level.ALL) =
        Level.run { _findLevel(level, default) }

    private val Level.level get() = with(this) { _level }
}
