package org.example.downloader.core;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.IntConsumer;
import java.util.regex.Pattern;

public abstract class Downloader {
    protected final URL pageURL;
    protected EpisodeFormat format;
    protected Download download;
    protected URL videoURL;

    protected Downloader(URL pageURL) throws MalformedURLException {
        this.pageURL = pageURL;
    }

    protected Downloader(String pageURLString) throws MalformedURLException {
        this(new URL(pageURLString));
    }

    /**
     * Generates a Download Object, which can be used to download the video.
     * <p>
     * Returns null if any complications occur (link without a valid video,
     * URL not openable, etc.). Also replaces already established direct download
     * links by searching for them in the specified {@code pageURL}.
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
     * URL not openable, etc.). Also replaces already established direct download
     * links by searching for them in the specified {@code pageURL}.
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
        if ((videoURL = generateVideoDownloadURL()) == null) {
            throw new MalformedURLException(getInvalidVideoMessage());
        }

        getEpisodeFormat();

        return getVideoDownload(fileString, onRead);
    }

    /**
     * Generates a Download Object, which can be used to download the video.
     * <p>
     * Returns null if any complications occur (link without a valid video,
     * URL not openable, etc.). Also replaces already established direct download
     * links by searching for them in the specified {@code pageURL}.
     * <p>
     * The location of the destination file is calculated by the given path
     * (has to be a directory) and format expression (defined by
     * {@link EpisodeFormat#format(String)}), which is used to
     * determine the name and extension of the resulting file.
     *
     * @param path             the path leading to the directory which should contain the file - is automatically created
     * @param formatExpression the formatting expression
     * @return a Download Object representing the video download
     * @throws MalformedURLException the exception that is thrown if the URL is no valid video
     */
    public Download generateVideoDownload(String path, String formatExpression) throws MalformedURLException {
        download = null;
        return getVideoDownload(path, formatExpression);
    }

    /**
     * Returns a Download Object, which can be used to download the video.
     * <p>
     * Returns null if any complications occur (link without a valid video,
     * URL not openable, etc.).
     * <p>
     * The difference between this method and {@link Downloader#generateVideoDownload(String)} is
     * that this doesn't generate a new {@link Download} by searching in the specified URL but rather
     * by the already established parameters. If this is the first time retrieving a {@code Download} object,
     * {@code generateVideoDownload(String)} will be called instead.
     *
     * @param fileString the path to the storing file - is automatically created
     * @return a Download Object representing the video download.
     * @throws MalformedURLException the exception that is thrown if the URL is no valid video
     */
    public Download getVideoDownload(String fileString) throws MalformedURLException {
        return getVideoDownload(fileString, (bytes) -> {
        });
    }

    /**
     * Returns a Download Object, which can be used to download the video.
     * <p>
     * Returns null if any complications occur (link without a valid video,
     * URL not openable, etc.).
     * <p>
     * The difference between this method and {@link Downloader#generateVideoDownload(String, IntConsumer)} is
     * that this doesn't generate a new {@link Download} by searching in the specified URL but rather
     * by the already established parameters. If this is the first time retrieving a {@code Download} object,
     * {@code generateVideoDownload(String, IntConsumer)} will be called instead.
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
    public Download getVideoDownload(String fileString, IntConsumer onRead) throws MalformedURLException {
        if (videoURL == null) {
            return generateVideoDownload(fileString, onRead);
        } else {
            try {
                File file = new File(fileString);
                Files.createParentDirs(file);
                download = new Download(videoURL, file, onRead);
            } catch (IOException e) {
                download = null;
            }
            return download;
        }
    }

    /**
     * Returns a Download Object, which can be used to download the video.
     * <p>
     * Returns null if any complications occur (link without a valid video,
     * URL not openable, etc.).
     * <p>
     * The difference between this method and {@link Downloader#generateVideoDownload(String, String)} is
     * that this doesn't generate a new {@link Download} by searching in the specified URL but rather
     * by the already established parameters. If this is the first time retrieving a {@code Download} object,
     * {@code generateVideoDownload(String, String)} will be called instead.
     * <p>
     * The location of the destination file is calculated by the given path
     * (has to be a directory) and format expression (defined by
     * {@link EpisodeFormat#format(String)}), which is used to
     * determine the name and extension of the resulting file.
     *
     * @param pathString       the path leading to the directory which should contain the file - is automatically created
     * @param formatExpression the formatting expression
     * @return a Download Object representing the video download
     * @throws MalformedURLException the exception that is thrown if the URL is no valid video
     */
    public Download getVideoDownload(String pathString, String formatExpression) throws MalformedURLException {
        String fileName = getEpisodeFormat().format(formatExpression);
        if (Files.getFileExtension(fileName).equals("")) {
            fileName = fileName.replaceAll(Pattern.quote("."), "") + "." + getDefaultFileExtension();
        }
        String fullPath = pathString + File.separator + fileName;
        fullPath = fullPath.replaceAll(Pattern.quote(File.separator + File.separator), File.separator);

        return getVideoDownload(fullPath);
    }

    /**
     * Returns the last retrieved Download object.
     *
     * @return the latest Download
     */
    public Download getLatestDownload() {
        return download;
    }

    /**
     * Generates an EpisodeFormat Object which can be used to
     * format the description of an episode to a user friendly
     * String.
     * <p>
     * This method generates the EpisodeFormat object and returns it.
     * It most likely requires some webscraping to form an {@code EpisodeFormat}.
     *
     * @return the EpisodeFormat of this Downloader
     */
    public EpisodeFormat generateEpisodeFormat() {
        this.format = generateEpisodeFormatNotSetting();
        return format;
    }

    /**
     * Gets an EpisodeFormat Object which can be used to
     * format the description of an episode to a user friendly
     * String.
     * <p>
     * The format is only generated by calling {@link Downloader#generateEpisodeFormat()}
     * if the format hasn't yet been generated once.
     *
     * @return the EpisodeFormat
     */
    public EpisodeFormat getEpisodeFormat() {
        return (format != null ? format : generateEpisodeFormat());
    }

    protected String getDefaultFileExtension() {
        return "mp4";
    }

    protected abstract URL generateVideoDownloadURL();

    protected abstract EpisodeFormat generateEpisodeFormatNotSetting();

    /**
     * Gets the error message when the Downloader can't find a
     * downloadable video on tbe specified page.
     *
     * @return the error message
     */
    protected abstract String getInvalidVideoMessage();
}
