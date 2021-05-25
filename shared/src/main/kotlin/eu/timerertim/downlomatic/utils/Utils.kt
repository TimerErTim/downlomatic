package eu.timerertim.downlomatic.utils

import eu.timerertim.downlomatic.utils.logging.Log
import kotlin.system.exitProcess

/**
 * Provides some neutral helper functions and helps in code reuse.
 */
object Utils {
    const val GENERIC_EXIT_CODE = 1
    const val CONNECTION_EXIT_CODE = 2
    const val ARGUMENT_EXIT_CODE = 3

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
        // Close Logging object
        Log.close()

        // Exit with the given errorCode
        exitProcess(errorCode)
    }
}