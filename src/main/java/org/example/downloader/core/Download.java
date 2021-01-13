package org.example.downloader.core;

import org.example.downloader.utils.ReadableConsumerByteChannel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.util.function.IntConsumer;

public class Download implements AutoCloseable {
    private final long size;
    private final FileOutputStream dest;
    private final ReadableConsumerByteChannel src;
    private long downloaded;
    private DownloadThread parallel;

    /**
     * Creates a download based on parameters
     *
     * @param dest          the destination file
     * @param urlConnection the downloaded file
     * @param onRead        the action on byte reads
     * @throws IOException Error when creating InputStream
     */
    public Download(FileOutputStream dest, URLConnection urlConnection, IntConsumer onRead) throws IOException {
        this.dest = dest;
        this.src = new ReadableConsumerByteChannel(Channels.newChannel(
                urlConnection.getInputStream()),
                (bytes) -> {
                    downloaded = bytes;
                    onRead.accept(bytes);
                });
        this.size = urlConnection.getContentLength();
        this.downloaded = 0;
    }

    /**
     * Starts the download. Note that this method is blocking until the download is completed.
     *
     * @return false if error occurred during download
     */
    public boolean startDownload() {
        try {
            downloaded = dest.getChannel().transferFrom(src, 0, Long.MAX_VALUE);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Starts the download. Note that this method is
     * not blocking and creates a dedicated DownloadThread
     * <p>
     * Note also that only one parallel download can run at a time.
     * Therefore calling this method a second time before the first
     * {@link DownloadThread} isn't finished will result in no action and
     * a {@code null} return value.
     *
     * @return null if there is already a DownloadThread running, otherwise
     * the DownloadThread responsible for executing this Download
     */
    public DownloadThread startParallel() {
        if (parallel == null) {
            //TODO: Implement
            return null;
        } else {
            return null;
        }
    }

    /**
     * Stops the download by closing underlying streams.
     */
    public void stop() {

    }

    /**
     * Returns true if the download is finished.
     * <p>
     * The download is finished when the amount of downloaded bytes
     * equals the should size of the downloaded file.
     *
     * @return true if download is finished
     */
    public boolean isFinished() {
        return downloaded == size;
    }

    /**
     * Stops the download and closes underlying streams.
     * <p>
     * Same as calling {@link Download#stop()} but this method does
     * not catch exceptions.
     *
     * @throws Exception thrown exception
     */
    @Override
    public void close() throws Exception {
        if (src.isOpen()) {
            dest.close();
            src.close();
        }
    }
}
