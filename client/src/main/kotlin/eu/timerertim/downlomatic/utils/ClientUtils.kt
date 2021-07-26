package eu.timerertim.downlomatic.utils

import com.sun.jna.Function
import com.sun.jna.platform.win32.WinDef.*
import com.sun.jna.platform.win32.WinNT.HANDLE
import eu.timerertim.downlomatic.console.ClientArgument
import eu.timerertim.downlomatic.console.ConsoleUtils
import eu.timerertim.downlomatic.console.ParsedArguments
import eu.timerertim.downlomatic.utils.logging.Level
import eu.timerertim.downlomatic.utils.logging.Log
import kotlin.system.exitProcess

/**
 * Provides some neat helper functions for the client.
 */
object ClientUtils {
    /**
     * Sets up the client to allow successful execution.
     */
    @JvmStatic
    @JvmOverloads
    fun setup(arguments: ParsedArguments = ConsoleUtils.parseArgs()) {
        // Invoke neutral Utils setup
        Utils.setup()


        // Setup logging
        if (arguments.hasArgument(ClientArgument.VERBOSE)) {
            Log.consoleVerbosity = Level.ALL
        }
        if (!arguments.hasArgument(ClientArgument.NO_FILE_LOGGING)) {
            Log.fileLogging = true
        }

        // Windows 10 VT100 Terminal - copied from Stackoverflow
        if (System.getProperty("os.name").startsWith("Windows")) {
            try {
                // Set output mode to handle virtual terminal sequences
                val enableVirtualTerminalProcessing = 4
                val getStdHandleFunc = Function.getFunction("kernel32", "GetStdHandle")
                val stdOutputHandle = DWORD(-11)
                val hOut = getStdHandleFunc.invoke(HANDLE::class.java, arrayOf<Any>(stdOutputHandle)) as HANDLE
                val pDWMode = DWORDByReference(DWORD(0))
                val getConsoleModeFunc = Function.getFunction("kernel32", "GetConsoleMode")
                getConsoleModeFunc.invoke(BOOL::class.java, arrayOf<Any>(hOut, pDWMode))
                val dwMode: DWORD = pDWMode.value
                dwMode.setValue((dwMode.toInt() or enableVirtualTerminalProcessing).toLong())
                val setConsoleModeFunc = Function.getFunction("kernel32", "SetConsoleMode")
                setConsoleModeFunc.invoke(BOOL::class.java, arrayOf<Any>(hOut, dwMode))
            } catch (e: Exception) {
                println("VT100 Emulation could not be activated for MS Windows: CLI presentation will look very bad")
            }
        }
    }

    /**
     * Closes and cleans system resources and exits the program. The given [errorCode] is returned to the OS
     * (defaults to 0).
     */
    @JvmStatic
    @JvmOverloads
    fun exit(errorCode: Int = 0): Nothing {
        // Close resources
        cleanup()

        // Exits program
        exitProcess(errorCode)
    }

    /**
     * Closes and cleans system resources and prepares the program to exit. This is basically [exit] without a program
     * shutdown.
     */
    @JvmStatic
    fun cleanup() {
        // Client specific cleanup

        // Close shared resources
        Utils.cleanup()
    }
}