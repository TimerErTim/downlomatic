package eu.timerertim.downlomatic.util

import com.mongodb.MongoClientException
import eu.timerertim.downlomatic.api.HostEntry
import eu.timerertim.downlomatic.api.RESTService
import eu.timerertim.downlomatic.api.VideoEntry
import eu.timerertim.downlomatic.console.ConsoleUtils
import eu.timerertim.downlomatic.console.ParsedArguments
import eu.timerertim.downlomatic.console.ServerArgument
import eu.timerertim.downlomatic.core.scraping.Scraper
import eu.timerertim.downlomatic.util.logging.Level
import eu.timerertim.downlomatic.util.logging.Log
import org.litote.kmongo.getCollection
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
        Log.consoleVerbosity = if (arguments.hasArgument(ServerArgument.VERBOSE)) Level.ALL else Level.INFO
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
        if (arguments.hasArgument(ServerArgument.CLEAR)) {
            val hostCollection = MongoDBConnection.hostDB.getCollection<HostEntry>("hosts")
            val hosts = hostCollection.find().toList().takeIf { it.isNotEmpty() }
            hosts?.also {
                Log.i("The following hosts will be cleared: ${it.joinToString { host -> host.domain }}")
                hostCollection.drop()
            }?.forEach {
                MongoDBConnection.videoDB.getCollection<VideoEntry>(it.domain).drop()
            }
        }

        // Setup fetcher hostConfig
        if (arguments.hasArgument(ServerArgument.IGNORE_REDUNDANCY)) {
            Scraper.patchRedundancy = false
        }

        // Setup rest service
        if (arguments.hasArgument(ServerArgument.PORT)) {
            RESTService.apiPort = arguments[ServerArgument.PORT]?.toIntOrNull()?.takeIf { it in 0..65535 }
                ?: ConsoleUtils.showErrorHelpMessage(
                    "Argument \"${ServerArgument.PORT}\" needs to be a number from 0 to 65535"
                )
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
        WebScrapers.close()

        // Close shared resources
        Utils.cleanup()
    }
}
