package eu.timerertim.downlomatic.core.download;

import eu.timerertim.downlomatic.utils.logging.Log;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

/**
 * Downloads a single file from an URL via HTTP protocol.
 */
public class Download {
    private final long size;
    private final LongConsumer onRead;
    private final URL srcURL;
    private final File destFile;

    private ReadableConsumerByteChannel src;
    private FileChannel dest;
    private long downloaded;
    private boolean paused;

    private Thread parallel;
    private Consumer<Boolean> actionAfterFinish;

    /**
     * Creates a download based on parameters.
     *
     * @param file   File object which is written to
     * @param url    URL referencing the the source of the download
     * @param onRead the action on byte reads
     * @throws IOException error when creating local fields
     */
    public Download(URL url, File file, LongConsumer onRead) throws IOException {
        this.srcURL = url;
        this.destFile = file;
        this.size = url.openConnection().getContentLength();
        this.onRead = onRead;
    }

    /**
     * Creates a download based on parameters.
     *
     * @param urlString  URL referencing the the source of the download
     * @param fileString Path to the file being written to (is newly created if possible)
     * @param onRead     the action on byte reads
     * @throws IOException error when creating local fields
     */
    public Download(String urlString, String fileString, LongConsumer onRead) throws IOException {
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
     * can be started by invoking {@link Download#startParallel()} or invoking this method
     * in another Thread.
     *
     * @return false if error occurred during download or a parallel download
     * has already started or the download couldn't finish
     */
    public boolean startDownload() {
        if (parallel == null || Thread.currentThread().equals(parallel)) {
            parallel = Thread.currentThread();
            try {
                if (!(paused && downloadedFileSynchronous())) {
                    downloaded = 0;
                }

                generateNewSourceChannel();
                generateNewDestinationChannel();

                dest.transferFrom(src, 0, Long.MAX_VALUE);

                src.close();
                dest.close();
                boolean finished = isFinished();
                if (finished) {
                    paused = false;
                }
                return finished;
            } catch (IOException e) {
                if (!(e instanceof ClosedByInterruptException))
                    paused = true;
                return false;
            } finally {
                updateDownloaded();
                parallel = null;
            }
        }
        return false;
    }

    /**
     * Starts the download. Note that this method is
     * not blocking and creates a dedicated DownloadThread.
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
            parallel = new DownloadThread(this, (this.actionAfterFinish = actionAfterFinish));
            parallel.start();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Starts the download. Note that this method is
     * not blocking and creates a dedicated DownloadThread.
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
        return startParallel((paused ? actionAfterFinish : b -> {
        }));
    }

    /**
     * Stops the download by interrupting the responsible thread.
     * <p>
     * Invoking this method (regardless of a parallel download running) will
     * tell this Download object to completely restart the download upon the
     * next invocation of {@link Download#startDownload()} or {@link Download#startParallel()}.
     * This behavior overwrites and can be overwritten by {@link Download#pause()};
     *
     * @return true if the download has been stopped, false if there was no download
     */
    public boolean stop() {
        boolean returnValue = parallel != null;
        if (returnValue) {
            parallel.interrupt();
        }
        paused = false;
        return returnValue;
    }

    /**
     * Pauses the download by interrupting the responsible thread.
     * <p>
     * Invoking this method (regardless of a parallel download running) will
     * tell this Download object to try to continue the download upon the
     * next invocation of {@link Download#startDownload()} or {@link Download#startParallel()}.
     * This behavior overwrites and can be overwritten by {@link Download#stop()};
     *
     * @return true if the download has been paused, false if there was no download
     */
    public boolean pause() {
        boolean returnValue = parallel != null;
        if (returnValue) {
            parallel.interrupt();
        }
        paused = true;
        return returnValue;
    }

    /**
     * Returns true if the download is running.
     * <p>
     * The download is running if a parallel download
     * is working in the background.
     *
     * @return true if download is running
     */
    public boolean isRunning() {
        return parallel != null;
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
        return getDownloaded() == size;
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
        return downloaded;
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
     * Returns the name of the destination file.
     *
     * @return the name of the destination file
     */
    public String getFileName() {
        return destFile.getName();
    }

    /**
     * Returns the URL of the source as String.
     *
     * @return the URL being downloaded from as String
     */
    public String getSourceURL() {
        return srcURL.toString();
    }

    /**
     * Returns false if the destination file's length
     * has been changed externally.
     * <p>
     * This should only be called without a parallel download
     * running as this method would 99% of the time return false
     * due to synchronization problems. To prevent these false negatives
     * this method always returns true if a parallel download is
     * running in the background. This behavior isn't useful, therefore
     * it is advised to wait for the parallel download to finish.
     *
     * @return true if file has not been changed externally
     */
    public boolean downloadedFileSynchronous() {
        if (parallel == null) {
            return destFile.length() == downloaded;
        } else {
            return true;
        }
    }

    /**
     * Returns a String representation of this object.
     *
     * @return a String representation of this object
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("srcURL", srcURL)
                .append("destFile", destFile)
                .append("downloaded", downloaded)
                .append("size", size)
                .append("paused", paused)
                .append("parallel", parallel)
                .build();
    }

    private void generateNewSourceChannel() throws IOException {
        URLConnection urlConnection = srcURL.openConnection();
        if (downloaded > 0) {
            urlConnection.setRequestProperty("Range", "bytes=" + downloaded + "-");
            if (!(urlConnection instanceof HttpURLConnection && ((HttpURLConnection) urlConnection).getResponseCode() == 206)) {
                downloaded = 0;
            }
        }
        src = new ReadableConsumerByteChannel(
                Channels.newChannel(urlConnection.getInputStream()),
                onRead,
                this);

    }

    private void generateNewDestinationChannel() throws IOException {
        if (dest == null || !dest.isOpen()) {
            dest = new FileOutputStream(destFile, true).getChannel();
        }
        dest.truncate(downloaded);
    }

    private void updateDownloaded() {
        try {
            src.close();
        } catch (IOException ex) {
            Log.w("Source ByteChannel could not be closed", ex);
        } finally {
            src = null;
        }
    }

    /**
     * Responsible for the execution of a {@code Download} without blocking.
     */
    private static class DownloadThread extends Thread {
        private static int ID = 0;

        private final Download referenceDownload;
        private final Consumer<Boolean> actionAfterFinish;

        public DownloadThread(Download referenceDownload, Consumer<Boolean> actionAfterFinish) {
            this.referenceDownload = referenceDownload;
            this.actionAfterFinish = actionAfterFinish;
            this.setName("Download Thread " + ID++);
        }

        @Override
        public void run() {
            boolean downloadSuccess = referenceDownload.startDownload();

            if (!referenceDownload.paused) {
                actionAfterFinish.accept(downloadSuccess);
            }
        }
    }

    private static class ReadableConsumerByteChannel implements ReadableByteChannel {
        private final ReadableByteChannel byteChannel;
        private final Download download;
        private final LongConsumer onRead;

        public ReadableConsumerByteChannel(ReadableByteChannel byteChannel, LongConsumer onRead, Download download) {
            this.byteChannel = byteChannel;
            this.onRead = onRead;
            this.download = download;
        }

        @Override
        public int read(ByteBuffer byteBuffer) throws IOException {
            int nRead = byteChannel.read(byteBuffer);
            notifyBytesRead(nRead);
            return nRead;
        }

        protected void notifyBytesRead(int nRead) {
            if (nRead <= 0) {
                return;
            }
            download.downloaded += nRead;
            onRead.accept(download.downloaded);
        }

        @Override
        public boolean isOpen() {
            return byteChannel.isOpen();
        }

        @Override
        public void close() throws IOException {
            byteChannel.close();
        }
    }
}
