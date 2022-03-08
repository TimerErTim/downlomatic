package eu.timerertim.downlomatic.util

import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.MongoSecurityException
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo
import java.util.logging.Level
import java.util.logging.Logger

/**
 * This object handles the connection to the MongoDB instance.
 */
object MongoDBConnection {
    private const val USER = "downlomaticUser"
    private const val PASSWORD = "wzrw<X/!8\$JQC=W&"
    private const val APPLICATION = "downlomatic"
    private const val HOST_DATABASE_NAME = "${APPLICATION}_HOSTS"
    private const val VIDEO_DATABASE_NAME = "${APPLICATION}_VIDEOS"

    val videoDB: MongoDatabase
    val hostDB: MongoDatabase

    init {
        // Logger deactivation
        Logger.getLogger("org.mongodb.driver").level = Level.SEVERE

        // Settings setup
        val settingsBuilder = MongoClientSettings.builder()
        settingsBuilder.applicationName(APPLICATION)

        // Settings generation
        val unauthSettings = settingsBuilder.build()
        val authSettings = settingsBuilder.credential(
            MongoCredential.createCredential(
                USER,
                APPLICATION,
                PASSWORD.toCharArray()
            )
        ).build()

        // Connect
        var client = KMongo.createClient(authSettings)

        // Get DB
        videoDB = try {
            client.getDatabase(VIDEO_DATABASE_NAME).also { it.getCollection("unused").find().forEach { } }
        } catch (e: MongoSecurityException) {
            client = KMongo.createClient(unauthSettings)
            client.getDatabase(VIDEO_DATABASE_NAME)
        }
        testConnection()
        hostDB = client.getDatabase(HOST_DATABASE_NAME)
    }

    /**
     * Tests the connection to the database. May throw several MongoDB exceptions.
     */
    @JvmStatic
    fun testConnection() {
        videoDB.getCollection("unused").find().forEach { }
    }
}
