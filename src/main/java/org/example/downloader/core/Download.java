package org.example.downloader.core;

import org.example.downloader.utils.ReadableConsumerByteChannel;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class Download implements Closeable {
    private final long size;
    private final File destFile;
    private final URL srcURL;
    private FileOutputStream dest;
    private final ReadableConsumerByteChannel src;
    private long downloaded;

    private DownloadThread parallel;

    /**
     * Creates a download based on parameters.
     *
     * @param file   File object which is written to
     * @param url    URL referencing the the source of the download
     * @param onRead the action on byte reads
     * @throws IOException error when creating local fields
     */
    public Download(URL url, File file, IntConsumer onRead) throws IOException {
        this.srcURL = url;
        this.destFile = file;
        URLConnection urlConnection = url.openConnection();
        this.size = urlConnection.getContentLength();
        this.src = new ReadableConsumerByteChannel(
                Channels.newChannel(urlConnection.getInputStream()),
                onRead);
    }

    /**
     * Creates a download based on parameters.
     *
     * @param urlString  URL referencing the the source of the download
     * @param fileString Path to the file being written to (is newly created if possible)
     * @param onRead     the action on byte reads
     * @throws IOException error when creating local fields
     */
    public Download(String urlString, String fileString, IntConsumer onRead) throws IOException {
        this(new URL(urlString), new File(fileString), onRead);
    }

    /**
     * Creates a download based on parameters.
     *
     * @param urlString  URL referencing the the source of the download
     * @param fileString Path to the file being written to (is newly created if possible)
     * @throws IOException error when creating local fields
     */
    public Download(String urlString, String fileString) throws IOException {
        this(urlString, fileString, (nRead) -> {
        });
    }

    /**
     * Starts the download. Note that this method is blocking until the download is completed.
     * <p>
     * Most download management methods in this class only work with a parallel download, which
     * can be started by invoking {@link Download#startParallel()}.
     *
     * @return false if error occurred during download or a parallel download
     * has already started or the download couldn't finish
     */
    public boolean startDownload() {
        if (parallel == null || Thread.currentThread().equals(parallel)) {
            try {
                dest.getChannel().position(0).truncate(0);
                downloaded = dest.getChannel().transferFrom(src, 0, Long.MAX_VALUE);
                return isFinished();
            } catch (IOException e) {
                return false;
            } finally {
                downloaded = getDownloaded();
            }
        }
        return false;
    }

    /**
     * Starts the download. Note that this method is
     * not blocking and creates a dedicated DownloadThread
     * <p>
     * Note also that only one parallel download can run at a time.
     * Therefore calling this method a second time before the first
     * {@link DownloadThread} isn't finished will result in no action and
     * a return value of false.
     * <p>
     * Because there is no way for a parallel download to notify it's original
     * Download Object (this Object), you can specify the action after the download
     * is finished with a {@link Consumer<>} Object. The {@code Consumer} takes in
     * a boolean, representing the success of the download. True means the download
     * was successful.
     * <p>
     * Most download management methods of this Download Object can only
     * be used if there is a parallel download running.
     *
     * @return a boolean representing the successfulness of starting a parallel download.
     */
    public boolean startParallel(Consumer<Boolean> actionAfterFinish) {
        if (parallel == null) {
            downloaded = 0;
            parallel = new DownloadThread(this, actionAfterFinish);
            parallel.start();
            return true;
        } else {
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
     * a return value of false.
     * <p>
     * Most download management methods of this Download Object can only
     * be used if there is a parallel download running.
     *
     * @return a boolean representing the successfulness of starting a parallel download.
     */
    public boolean startParallel() {
        return startParallel((b) -> {
        });
    }

    /**
     * Stops the download by interrupting the responsible thread.
     * <p>
     * Note that this inevitably closes every stream, so it's effectively the same
     * as calling {@link Download#close()} with the only difference is being able to
     * call this method only during a parallel download. Otherwise this method will
     * have no effect.
     *
     * @return true if the download has been stopped, false if there was no download
     */
    public boolean stop() {
        if (parallel != null) {
            parallel.interrupt();
            return true;
        }
        return false;
    }

    /**
     * Stops the download and closes underlying streams.
     * <p>
     * Doesn't immediately stop parallel {@code Download}s.
     * It only marks them for automatic closure upon completion.
     *
     * @throws IOException thrown exception
     */
    @Override
    public void close() throws IOException {
        if (src.isOpen() && parallel == null) {
            dest.close();
            src.close();
        } else if (src.isOpen() && parallel != null) {
            parallel.attemptedClose();
        }
    }

    /**
     * Returns whether or not this Download is open.
     *
     * @return the open status
     */
    public boolean isOpen() {
        return src.isOpen();
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
     * Returns true if the file is already completely downloaded
     * before even starting the download.
     *
     * @return the file already being downloaded
     */
    public boolean isAlreadyDownloaded() {
        if (destFile.length() == size) {
            downloaded = size;
            return true;
        } else
            return false;
    }

    /**
     * Returns the amount of downloaded bytes.
     *
     * @return the downloaded size
     */
    public long getDownloaded() {
        return (isFinished() ? downloaded : (src.getTotalByteRead() + downloaded) % size);
    }

    /**
     * Returns the amount of data that has to be downloaded.
     *
     * @return the resulting file size
     */
    public long getSize() {
        return size;
    }

    /**
     * Returns a String representation of this Object.
     * <p>
     * It does so by displaying the number of downloaded bytes out
     * of the pending bytes after the name of the parallel executing Thread,
     * if there even is one.
     * <p>
     * Example 1:<b> Download Thread 1: 123000B/312333B</b><br>
     * Example 2:<b> 200000B/200000B</b>
     *
     * @return a String representation of this Object
     */
    @Override
    public String toString() {
        return (parallel != null ? parallel.getName() + ": " : "") + getDownloaded() + "B/" + size + "B";
    }

    /**
     * Responsible for the execution of a {@code Download} without blocking.
     */
    private static class DownloadThread extends Thread {
        private static int ID = 0;

        private final Download referenceDownload;
        private final Consumer<Boolean> actionAfterFinish;
        private boolean closeAfterFinish;

        public DownloadThread(Download referenceDownload, Consumer<Boolean> actionAfterFinish) {
            this.referenceDownload = referenceDownload;
            this.actionAfterFinish = actionAfterFinish;
            this.closeAfterFinish = false;
            this.setName("Download Thread " + ID++);
        }

        public void attemptedClose() {
            closeAfterFinish = true;
        }

        @Override
        public void run() {
            boolean downloadSuccess = referenceDownload.startDownload();

            actionAfterFinish.accept(downloadSuccess);

            referenceDownload.parallel = null;
            if (!downloadSuccess || closeAfterFinish) {
                try {
                    referenceDownload.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
