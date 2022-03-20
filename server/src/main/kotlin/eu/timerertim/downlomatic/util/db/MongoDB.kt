package eu.timerertim.downlomatic.util.db

import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.MongoSecurityException
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.IndexOptions
import eu.timerertim.downlomatic.core.db.DownloaderEntry
import eu.timerertim.downlomatic.core.db.HostEntry
import eu.timerertim.downlomatic.core.db.VideoEntry
import eu.timerertim.downlomatic.util.logging.Log
import io.ktor.utils.io.core.*
import org.litote.kmongo.KMongo
import org.litote.kmongo.ensureIndex
import org.litote.kmongo.getCollection
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

private const val USER = "downlomaticUser"
private const val PASSWORD = "wzrw<X/!8\$JQC=W&"
private const val APPLICATION = "downlomatic"

/**
 * This object handles the connection to the MongoDB instance.
 */
object MongoDB : Closeable, MongoDatabase by establishDatabaseConnection() {
    val hostCollection = getCollection<HostEntry>("hosts")
    val videoCollection = getCollection<VideoEntry>("videos")
    val downloaderCollection = getCollection<DownloaderEntry>("downloaders")

    init {
        testConnection()

        val expireAfter = IndexOptions().expireAfter(0, TimeUnit.SECONDS)
        downloaderCollection.ensureIndex(DownloaderEntry::expireAt, indexOptions = expireAfter)
    }

    /**
     * Tests the connection to the database. May throw several MongoDB exceptions.
     */
    @JvmStatic
    fun testConnection() {
        listCollectionNames().toList()
    }

    override fun close() {
        val client = mongoClient
        if (client != null) {
            Log.i("Closing connection to MongoDB...")
            client.close()
            mongoClient = null
        }
    }
}

private var mongoClient: MongoClient? = null

private fun establishDatabaseConnection(): MongoDatabase {
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
    val client = try {
        KMongo.createClient(authSettings).also {
            it.listDatabaseNames().toList()
        }
    } catch (ex: MongoSecurityException) {
        KMongo.createClient(unauthSettings)
    }
    mongoClient = client

    return client.getDatabase(APPLICATION)
}
