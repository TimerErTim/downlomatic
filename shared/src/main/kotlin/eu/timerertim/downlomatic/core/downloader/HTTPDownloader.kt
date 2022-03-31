package eu.timerertim.downlomatic.core.downloader


import eu.timerertim.downlomatic.core.descriptor.HTTPDescriptor
import kotlinx.coroutines.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.channels.ReadableByteChannel

@Serializable
@SerialName("HTTPDownloader")
class HTTPDownloader(override val descriptor: HTTPDescriptor) : Downloader<HTTPDescriptor>() {
    @Transient
    private var downloadJob: Job? = null

    @Transient
    private var sourceChannel: ReadableByteChannel? = null

    @Transient
    private var destinationChannel: FileChannel? = null

    override suspend fun fileExtension() = descriptor.url.file.substringAfterLast(".", super.fileExtension())

    override suspend fun pauseDownload() {
        withContext(Dispatchers.IO) {
            sourceChannel?.close()
        }
        downloadJob?.join()
        downloadJob = null
    }

    override suspend fun startDownload() {
        downloadJob = downloadJob ?: runBlocking {
            val connection = createURLConnection()
            val fileSize = withContext(Dispatchers.IO) {
                destinationFile.length()
            }

            CoroutineScope(Dispatchers.IO).launch {
                sourceChannel = generateSourceChannel(connection)
                size = connection.contentLengthLong
                if (fileSize != size) {
                    destinationChannel = generateDestinationChannel()

                    destinationChannel?.transferFrom(sourceChannel, 0, Long.MAX_VALUE)
                    sourceChannel?.close()
                    destinationChannel?.close()
                }
            }
        }
    }

    override suspend fun stopDownload() {
        withContext(Dispatchers.IO) {
            sourceChannel?.close()
            destinationChannel?.close()
        }
    }

    override suspend fun joinDownload(): Boolean {
        downloadJob?.join()
        downloadJob = null
        return destinationFile.length() == size
    }

    private fun createURLConnection(): HttpURLConnection {
        val connection = descriptor.url.openConnection() as HttpURLConnection
        connection.requestMethod = descriptor.method
        for (header in descriptor.headers) {
            connection.setRequestProperty(header.key, header.value)
        }
        return connection
    }

    private fun openURLConnection(connection: HttpURLConnection): InputStream {
        if (descriptor.request != null) {
            connection.outputStream.writer().write(descriptor.request)
        }
        return connection.inputStream
    }

    @Throws(IOException::class)
    private fun generateSourceChannel(urlConnection: HttpURLConnection = createURLConnection()): ReadableByteChannel {
        val inputStream = if (downloadedBytes > 0) {
            urlConnection.setRequestProperty("Range", "bytes=$downloadedBytes-")
            val stream = openURLConnection(urlConnection)
            if (urlConnection.responseCode != 206) {
                downloadedBytes = 0
            }
            stream
        } else openURLConnection(urlConnection)
        return ReadableListeningByteChannel(Channels.newChannel(inputStream)) { downloadedBytes += it }
    }

    @Throws(IOException::class)
    private fun generateDestinationChannel(): FileChannel {
        val currentDestinationChannel = destinationChannel
        val dest = if (currentDestinationChannel == null || !currentDestinationChannel.isOpen) {
            destinationFile.parentFile.mkdirs()
            FileOutputStream(destinationFile, true).channel
        } else {
            currentDestinationChannel
        }
        dest.truncate(downloadedBytes)
        return dest
    }

    private class ReadableListeningByteChannel(
        private val byteChannel: ReadableByteChannel,
        private val onRead: (Int) -> Unit
    ) : ReadableByteChannel by byteChannel {
        @Throws(IOException::class)
        override fun read(byteBuffer: ByteBuffer): Int {
            val nRead = byteChannel.read(byteBuffer)
            notifyBytesRead(nRead)
            return nRead
        }

        private fun notifyBytesRead(nRead: Int) {
            if (nRead <= 0) {
                return
            }
            onRead(nRead)
        }
    }
}
