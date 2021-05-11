package eu.timerertim.downlomatic.utils

import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.MongoSecurityException
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo

/**
 * This object handles the connection to the MongoDB instance.
 */
object MongoDBConnection {
    private const val USER = "downlomaticUser"
    private const val PASSWORD = "wzrw<X/!8\$JQC=W&"
    private const val APPLICATION = "downlomatic"

    val db: MongoDatabase

    init {
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
        val client = KMongo.createClient(authSettings)

        // Get DB
        db = try {
            client.getDatabase(APPLICATION).also { it.getCollection("unused").find().forEach { } }
        } catch (e: MongoSecurityException) {
            KMongo.createClient(unauthSettings).getDatabase(APPLICATION)
        }
    }

    /**
     * Tests the connection to the database. May throw several MongoDB exceptions.
     */
    @JvmStatic
    fun testConnection() {
        db.getCollection("unused").find().forEach { }
    }
}