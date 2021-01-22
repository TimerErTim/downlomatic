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

            EpisodeFormat format = null;
            if (this.format == null) {
                format = generateEpisodeFormat();
            }
            this.format = format;

            File file = new File(fileString);
            Files.createParentDirs(file);
            download = new Download(videoURL, file, onRead);
            return download;
        } catch (MalformedURLException e) {
            throw e;
        } catch (IOException e) {
            return null;
        }
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
     * <p>
     * The location of the destination file is calculated by the given path
     * (has to be a directory) and format expression (defined by
     * {@link EpisodeFormat#format(String)}), which is used to
     * determine the name and extension of the resulting file.
     *
     * @param path             the path leading to the directory which should contain the file - is automatically created
     * @param formatExpression the
     * @param onRead           the action on byte reads
     * @return a Download Object representing the video download.
     * @throws MalformedURLException the exception that is thrown if the URL is no valid video
     */
    public Download generateVideoDownload(String path, String formatExpression, IntConsumer onRead) throws MalformedURLException {
        if (this.format == null) {
            this.format = generateEpisodeFormat();
        }

        String fileName = format.format(formatExpression);
        if (Files.getFileExtension(fileName).equals("")) {
            fileName = fileName.replaceAll(Pattern.quote("."), "") + "." + getDefaultFileExtension();
        }
        String fullPath = path + File.separator + fileName;
        fullPath = fullPath.replaceAll(Pattern.quote(File.separator + File.separator), File.separator);

        return generateVideoDownload(fullPath, onRead);
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

    protected abstract EpisodeFormat generateEpisodeFormatNotSetting();

    /**
     * Gets an EpisodeFormat Object which can be used to
     * format the description of an episode to a user friendly
     * String.
     * <p>
     * Note though, that in order to get a non empty EpisodeFormat,
     * you need to invoke {@link Downloader#generateEpisodeFormat()}
     * or {@link Downloader#generateVideoDownload(String)} prior to
     * calling this method.
     *
     * @return the EpisodeFormat
     */
    public EpisodeFormat getEpisodeFormat() {
        return (format != null ? format : new EpisodeFormat.EpisodeFormatGenerator().generate());
    }

    protected String getDefaultFileExtension() {
        return "mp4";
    }

    protected abstract URL generateVideoDownloadURL();

    /**
     * Returns whether or not this Downloader
     * requires JavaScript in order to generate a
     * Download.
     * <p>
     * Useful for guessing the computation time when generating
     * a {@link Download}.
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
    protected abstract String getInvalidVideoMessage();
}
