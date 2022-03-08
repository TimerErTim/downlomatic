package eu.timerertim.downlomatic.core.download

import eu.timerertim.downlomatic.core.video.Video
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URLConnection
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.channels.ReadableByteChannel

class HTTPDownload(video: Video, targetFile: File) : Download(video, targetFile) {
    private val realTargetFile get() = File("${targetFile.absolutePath}.$fileType")

    private var downloadJob: Job? = null
    private var sourceChannel: ReadableByteChannel? = null
    private var destinationChannel: FileChannel? = null

    override suspend fun _pauseDownload() {
        withContext(Dispatchers.IO) {
            sourceChannel?.close()
        }
        state = DownloadState.Paused
    }

    override suspend fun _startDownload() {
        downloadJob = downloadJob ?: runBlocking {
            launch {
                val fileSize = withContext(Dispatchers.IO) {
                    size = url.openConnection().contentLengthLong
                    realTargetFile.length()
                }
                state = DownloadState.Downloading

                if (fileSize != size) {
                    sourceChannel = generateSourceChannel()
                    destinationChannel = generateDestinationChannel()

                    withContext(Dispatchers.IO) {
                        destinationChannel?.transferFrom(sourceChannel, 0, Long.MAX_VALUE)
                        sourceChannel?.close()
                        destinationChannel?.close()
                    }
                }
            }
        }
        downloadJob?.join()
        downloadJob = null
        if (state is DownloadState.Downloading) state = DownloadState.Finished
    }

    override suspend fun _stopDownload() {
        withContext(Dispatchers.IO) {
            sourceChannel?.close()
            destinationChannel?.close()
        }
    }

    override suspend fun joinDownload() {
        downloadJob?.join()
    }

    @Throws(IOException::class)
    private fun generateSourceChannel(): ReadableByteChannel {
        val urlConnection: URLConnection = url.openConnection()
        if (downloadedBytes > 0) {
            urlConnection.setRequestProperty("Range", "bytes=$downloadedBytes-")
            if (!(urlConnection is HttpURLConnection && urlConnection.responseCode == 206)) {
                downloadedBytes = 0
            }
        }
        return ReadableDownloadByteChannel(
            Channels.newChannel(urlConnection.getInputStream()),
            download = this@HTTPDownload
        )
    }

    @Throws(IOException::class)
    private fun generateDestinationChannel(): FileChannel {
        val currentDestinationChannel = destinationChannel
        val dest = if (currentDestinationChannel == null || !currentDestinationChannel.isOpen) {
            realTargetFile.parentFile.mkdirs()
            FileOutputStream(realTargetFile, true).channel
        } else {
            currentDestinationChannel
        }
        dest.truncate(downloadedBytes)
        return dest
    }

    private class ReadableDownloadByteChannel(
        private val byteChannel: ReadableByteChannel,
        private val onRead: (Long) -> Unit = { },
        private val download: Download
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
            download.downloadedBytes += nRead.toLong()
            onRead(download.downloadedBytes)
        }
    }
}
