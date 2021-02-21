package org.example.downloader.core.framework;

import com.google.common.io.Files;
import org.example.downloader.core.download.Download;
import org.example.downloader.core.format.EpisodeFormat;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.IntConsumer;
import java.util.regex.Pattern;

public abstract class Downloader {
    protected final URL pageURL;
    private EpisodeFormat format;
    private Download download;
    private URL videoURL;

    protected Downloader(URL pageURL) {
        this.pageURL = pageURL;
    }

    protected Downloader(String pageURLString) throws MalformedURLException {
        this(new URL(pageURLString));
    }

    /**
     * This methods generates the new URL leading to
     * the video. Overwrites the previously determined
     * URL.
     *
     * @return the newly generated {@code URL}
     */
    public URL generateVideoURL() {
        return (videoURL = generateVideoDownloadURL());
    }

    /**
     * Returns a Download Object, which can be used to download the video.
     * <p>
     * Returns null if any complications occur (URL not openable, No access to given File, etc.).
     * <p>
     * If this is the first time retrieving a {@code Download} object, the URL leading to the video
     * will be determined by calling {@link Downloader#generateVideoURL()}. Note that you can always
     * regenerate the video URL by invoking {@code generateVideoURL()} again.
     *
     * @param fileString the path to the storing file - is automatically created
     * @return a Download Object representing the video download.
     * @throws MalformedURLException the exception that is thrown if the URL is no valid video
     */
    public Download generateVideoDownload(String fileString) throws MalformedURLException {
        return generateVideoDownload(fileString, (bytes) -> {
        });
    }

    /**
     * Returns a new Download Object, which can be used to download the video.
     * <p>
     * Returns null if any complications occur (URL not openable, No access to given File, etc.).
     * <p>
     * If this is the first time retrieving a {@code Download} object, the URL leading to the video
     * will be determined by calling {@link Downloader#generateVideoURL()}. Note that you can always
     * regenerate the video URL by invoking {@code generateVideoURL()} again.
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
        if (videoURL == null && generateVideoURL() == null) {
            throw new MalformedURLException(getInvalidVideoMessage());
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
     * Returns null if any complications occur (URL not openable, No access to given File, etc.).
     * <p>
     * If this is the first time retrieving a {@code Download} object, the URL leading to the video
     * will be determined by calling {@link Downloader#generateVideoURL()}. Note that you can always
     * regenerate the video URL by invoking {@code generateVideoURL()} again.
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
    public Download generateVideoDownload(String pathString, String formatExpression) throws MalformedURLException {
        String fileName = getEpisodeFormat().format(formatExpression);
        if (Files.getFileExtension(fileName).equals("")) {
            fileName = fileName.replaceAll(Pattern.quote("."), "") + "." + getDefaultFileExtension();
        }
        String fullPath = pathString + File.separator + fileName;
        fullPath = fullPath.replaceAll(Pattern.quote(File.separator) + "+", File.separator);

        return generateVideoDownload(fullPath);
    }

    /**
     * Returns the last retrieved Download object.
     * <p>
     * Can be seen as a different form of {@code generateVideoDownload}
     * with the difference being that {@code generateVideoDownload}
     * generates a new {@link Download} while this method
     * returns the latest already generated {@code Download} object.
     * <p>
     * Note that by itself {@code generateVideoDownload} doesn't
     * refresh already established paths and URLs.
     *
     * @return the latest Download
     */
    public Download getVideoDownload() {
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
     * @throws MalformedURLException if the EpisodeFormat could not be retrieved from
     *                               given URL
     */
    public EpisodeFormat generateEpisodeFormat() throws MalformedURLException {
        this.format = generateEpisodeFormatNotSetting();
        if (format == null) {
            throw new MalformedURLException(getInvalidVideoMessage());
        }
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
     * @throws MalformedURLException if the EpisodeFormat could not be retrieved from
     *                               given URL
     */
    public EpisodeFormat getEpisodeFormat() throws MalformedURLException {
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
