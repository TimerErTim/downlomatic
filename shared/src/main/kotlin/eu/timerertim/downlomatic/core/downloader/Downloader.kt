package eu.timerertim.downlomatic.core.downloader

import eu.timerertim.downlomatic.core.descriptor.Descriptor
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.io.File

@Serializable
sealed class Downloader<out D : Descriptor> {
    abstract val descriptor: D

    @Transient
    protected lateinit var destinationFile: File
        private set

    @Transient
    var size = -1L
        protected set

    @Transient
    var downloadedBytes = 0L
        protected set(value) {
            if (value < field) {
                obsoleteBytes += field - value
            }
            field = value
        }

    @Transient
    var obsoleteBytes = 0L
        private set

    suspend fun initialize(destinationBaseFile: File) {
        this.destinationFile = File("${destinationBaseFile.path}.${fileExtension()}")
    }

    open suspend fun fileExtension() = "mp4"

    abstract suspend fun startDownload()
    abstract suspend fun pauseDownload()
    abstract suspend fun stopDownload()
    abstract suspend fun joinDownload(): Boolean
}
