package eu.timerertim.downlomatic.util.db

import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
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

var dbUser: String? = null
var dbPassword: String? = null
var dbVerifier: String? = null

private const val APPLICATION = "downlomatic"

/**
 * This object handles the connection to the MongoDB instance.
 */
object MongoDB : Closeable, MongoDatabase by establishDatabaseConnection() {
    val hostCollection = getCollection<HostEntry>("hosts")
    val videoCollection = getCollection<VideoEntry>("videos")
    val downloaderCollection by lazy {
        val collection = getCollection<DownloaderEntry>("downloaders")

        val expireAfter = IndexOptions().expireAfter(0, TimeUnit.SECONDS)
        collection.ensureIndex(DownloaderEntry::expireAt, indexOptions = expireAfter)
        collection
    }

    init {
        Log.d("Initializing MongoDB Object")
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
    val dbUser = dbUser
    if (dbUser != null) {
        settingsBuilder.credential(
            MongoCredential.createCredential(
                dbUser,
                dbVerifier ?: APPLICATION,
                dbPassword?.toCharArray() ?: charArrayOf()
            )
        )
    }

    val settings = settingsBuilder.build()

    // Connect
    val client = KMongo.createClient(settings)
    mongoClient = client

    return client.getDatabase(APPLICATION)
}
