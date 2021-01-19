package org.example.downloader.core;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.IntConsumer;

public abstract class Downloader {
    protected final URL pageURL;
    protected Download download;
    protected URL videoURL;

    public Downloader(URL pageURL) throws MalformedURLException {
        this.pageURL = pageURL;
    }

    public Downloader(String pageURLString) throws MalformedURLException {
        this(new URL(pageURLString));
    }

    /**
     * Generates a Download Object, which can be used to download the video.
     * <p>
     * Returns null if any complications occur.
     *
     * @param fileString the path to the storing file - is automatically created
     * @return a Download Object representing the video download.
     * @throws MalformedURLException the exception that is thrown if the URL is no valid video
     */
    public Download generateVideoDownload(String fileString) throws MalformedURLException {
        return generateVideoDownload(fileString, (r) -> {
        });
    }

    /**
     * Generates a Download Object, which can be used to download the video.
     * <p>
     * Returns null if any complications occur (link without a valid video,
     * URL not openable, etc.).
     * <p>
     * onRead makes it possible to compute and keep track of progress, because
     * everytime a new buffer of bytes is read, onRead is called with the current
     * amount of bytes downloaded as parameter.
     *
     * @param fileString the path to the storing file - is automatically created
     * @param onRead     the action on byte reads
     * @return a Download Object representing the video download.
     * @throws MalformedURLException the exception that is thrown if the URL is no valid video
     */
    public Download generateVideoDownload(String fileString, IntConsumer onRead) throws MalformedURLException {
        try {
            if ((videoURL = generateVideoDownloadURL()) == null) {
                throw new MalformedURLException(getInvalidVideoMessage());
            }

            download = new Download(videoURL, new File(fileString), onRead);
            return download;
        } catch (MalformedURLException e) {
            throw e;
        } catch (IOException e) {
            return null;
        }
    }

    protected abstract URL generateVideoDownloadURL();

    /**
     * Returns whether or not this Downloader
     * requires JavaScript in order to generate a
     * Download.
     *
     * @return if JavaScript is required for execution
     */
    public abstract boolean needsJavaScript();

    /**
     * Gets the error message when the Downloader can't find a
     * downloadable video on tbe specified page.
     *
     * @return the error message
     */
    public abstract String getInvalidVideoMessage();
}
