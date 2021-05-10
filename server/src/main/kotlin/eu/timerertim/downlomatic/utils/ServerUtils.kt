package eu.timerertim.downlomatic.utils

import com.mongodb.MongoClientException
import eu.timerertim.downlomatic.utils.logging.Level
import eu.timerertim.downlomatic.utils.logging.Log

/**
 * Provides some neat helper functions for the server.
 */
object ServerUtils {

    /**
     * Sets up the server to allow successful execution.
     */
    @JvmStatic
    fun setup() {
        // Invoke neutral Utils setup
        Utils.setup()


        // Setup logging
        Log.fileLogging = true
        Log.consoleVerbosity = Level.WARN

        // Setup database connection
        try {
            try {
                DownlomaticDatabase.testConnection()
            } catch (exception: ExceptionInInitializerError) {
                throw exception.cause!!
            }
        } catch (exception: MongoClientException) {
            Log.f(
                "An error occurred while attempting to connect to the local MongoDB. Make sure MongoDB is " +
                        "running and has authorization deactivated!", exception
            )
            exit(2)
        }
    }

    /**
     * Closes and cleans system resources and prepares the program to exit. The given [errorCode] is returned to the OS
     * (defaults to 0).
     */
    @JvmStatic
    @JvmOverloads
    fun exit(errorCode: Int = 0) {

        Utils.exit(errorCode)
    }
}