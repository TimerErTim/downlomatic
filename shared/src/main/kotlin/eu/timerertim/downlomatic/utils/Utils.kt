package eu.timerertim.downlomatic.utils

import com.sun.jna.Function
import com.sun.jna.platform.win32.WinDef.*
import com.sun.jna.platform.win32.WinNT.HANDLE
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import kotlin.system.exitProcess

/**
 * Provides some neutral helper functions and helps in code reuse.
 */
object Utils {
    private val options: Options? = null
    private val help: HelpFormatter? = null

    /**
     * Initializes stuff which is needed for
     * successful execution.
     */
    fun initializeSetup() {
        // Windows 10 VT100 Terminal - copied from Stackoverflow
        if (System.getProperty("os.name").startsWith("Windows")) {
            try {
                // Set output mode to handle virtual terminal sequences
                val GetStdHandleFunc = Function.getFunction("kernel32", "GetStdHandle")
                val STD_OUTPUT_HANDLE = DWORD(-11)
                val hOut = GetStdHandleFunc.invoke(HANDLE::class.java, arrayOf<Any>(STD_OUTPUT_HANDLE)) as HANDLE
                val p_dwMode = DWORDByReference(DWORD(0))
                val GetConsoleModeFunc = Function.getFunction("kernel32", "GetConsoleMode")
                GetConsoleModeFunc.invoke(BOOL::class.java, arrayOf<Any>(hOut, p_dwMode))
                val ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4
                val dwMode = p_dwMode.value
                dwMode.setValue((dwMode.toInt() or ENABLE_VIRTUAL_TERMINAL_PROCESSING).toLong())
                val SetConsoleModeFunc = Function.getFunction("kernel32", "SetConsoleMode")
                SetConsoleModeFunc.invoke(BOOL::class.java, arrayOf<Any>(hOut, dwMode))
            } catch (e: Exception) {
                println("VT100 Emulation could not be activated for Windows OS: CLI presentation will look like bullshit")
            }
        }
    }

    /**
     * Basically the counterpart to [Utils.initializeSetup].
     *
     *
     * Closes every global resource and performs cleanup on exit.
     */
    fun onExit() {

    }

    /**
     * Initializes stuff which is needed for
     * successful execution.
     */
    @JvmStatic
    fun setup() {
        // Empty
    }

    /**
     * Exits the program with the given [errorCode]. Additionally, it closes and cleans everything not needed anymore.
     */
    @JvmStatic
    fun exit(errorCode: Int) {

        // Exit with the given errorCode
        exitProcess(errorCode)
    }
}