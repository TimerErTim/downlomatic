package eu.timerertim.downlomatic.utils

import kotlin.system.exitProcess

/**
 * Provides some neutral helper functions and helps in code reuse.
 */
object Utils {
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