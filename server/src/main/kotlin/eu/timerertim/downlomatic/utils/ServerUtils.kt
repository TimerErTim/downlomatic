package eu.timerertim.downlomatic.utils

import com.mongodb.MongoClientException
import eu.timerertim.downlomatic.console.ConsoleUtils
import eu.timerertim.downlomatic.console.ParsedArguments
import eu.timerertim.downlomatic.console.ServerArgument
import eu.timerertim.downlomatic.utils.logging.Level
import eu.timerertim.downlomatic.utils.logging.Log
import kotlin.system.exitProcess

/**
 * Provides some neat helper functions for the server.
 */
object ServerUtils {
    /**
     * Sets up the server to allow successful execution.
     */
    @JvmStatic
    @JvmOverloads
    fun setup(arguments: ParsedArguments = ConsoleUtils.parseArgs()) {
        // Invoke neutral Utils setup
        Utils.setup()


        // Setup logging
        Log.consoleVerbosity = if (arguments.hasArgument(ServerArgument.VERBOSE)) Level.ALL
        else Level.INFO
        if (!arguments.hasArgument(ServerArgument.NO_FILE_LOGGING)) {
            Log.fileLogging = true
        }

        // Setup database connection
        try {
            try {
                MongoDBConnection.testConnection()
                Log.i("MongoDB connection successfully established")
            } catch (exception: ExceptionInInitializerError) {
                throw exception.cause!!
            }
        } catch (exception: MongoClientException) {
            Log.f(
                "An error occurred while attempting to connect to the local MongoDB. Make sure MongoDB is " +
                        "running and has authorization deactivated!", exception
            )
            exit(Utils.CONNECTION_EXIT_CODE)
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
        // Server specific cleanup

        // Close shared resources
        Utils.cleanup()
    }
}