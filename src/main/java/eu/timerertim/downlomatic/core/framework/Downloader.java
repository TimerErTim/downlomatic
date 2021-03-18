package eu.timerertim.downlomatic.core.framework;

import com.google.common.io.Files;
import eu.timerertim.downlomatic.core.download.Download;
import eu.timerertim.downlomatic.core.format.EpisodeFormat;
import eu.timerertim.downlomatic.utils.logging.Log;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.IntConsumer;
import java.util.regex.Pattern;

/**
 * Represents a single video on a website and is used to retrieve a {@link Download}.
 */
public abstract class Downloader {
    /**
     * The page this {@code Downloader} is defined by. Retrieved from {@link Downloader#getPage()}
     */
    protected final Page page;
    private final URL pageURL;
    private EpisodeFormat format;
    private Download download;
    private URL videoURL;

    /**
     * Creates a {@code Downloader} object from the URL.
     *
     * @param pageURL the URL
     * @throws MalformedURLException the exception that is thrown if the URL is not valid
     */
    protected Downloader(URL pageURL) throws MalformedURLException {
        this.pageURL = pageURL;
        page = getPage();
        if (!isValidVideoURL(pageURL)) {
            throw new MalformedURLException("URL \"" + pageURL + "\" is no URL leading to a " + page.getPageDomain() + " video");
        }
    }

    /**
     * Creates a {@code Downloader} object from the URL.
     *
     * @param pageURLString the String representing URL to video site
     * @throws MalformedURLException the exception that is thrown if the URL is not valid
     */
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
    public URL generateVideoURL() throws MalformedURLException {
        setDownloader();
        return videoURL;
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
                Log.e("Download could not be created.", e);
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
        setDownloader();
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

    /**
     * Gets the error message when the Downloader can't find a
     * downloadable video on tbe specified page.
     *
     * @return the error message
     */
    protected String getInvalidVideoMessage() {
        return "URL \"" + pageURL + "\" is no valid video on " + page.getPageDomain();
    }

    /**
     * Returns the file extension that is used if nothing else is specified by the user.
     *
     * @return the default file extension
     */
    protected String getDefaultFileExtension() {
        return "mp4";
    }

    /**
     * Returns the {@code Page} that is associated with this {@code Downloader}.
     * <p>
     * Is used to set {@link Downloader#page}.
     *
     * @return the associated {@code Page}.
     */
    public abstract Page getPage();

    /**
     * Checks the URL for structural validity.
     *
     * @param url the URL to be checked
     * @return true if the URL is valid
     */
    protected abstract boolean isValidVideoURL(URL url);

    /**
     * Parses an {@code EpisodeFormat} and the download URL from the video URL.
     * <p>
     * The download URL is used to download the video and must lead directly to the video file. The {@link EpisodeFormat}
     * is used to format the video similar to {@link java.time.format.DateTimeFormatter}. You set values like
     * language, title, translation type and those values can be formatted to a String with
     * {@link EpisodeFormat#format(String)}.
     * <p>
     * This is done in one method because it allows for only one request with the
     * {@link eu.timerertim.downlomatic.utils.WebScrapers}. In case that can't be done because of the website's
     * structure, there's always the opportunity to send requests for multiple URLs one after another.
     * <p>
     * If any problems occur, null is returned.
     *
     * @param pageURL the URL under which one should be able to find the video
     * @return a {@code Pair} containing both the download URL and an {@code EpisodeFormat} object retrieved from the
     * video URL, or null if errors occurred
     */
    protected abstract Pair<URL, EpisodeFormat> parseDownloader(URL pageURL);

    private void setDownloader() throws MalformedURLException {
        Pair<URL, EpisodeFormat> params = parseDownloader(pageURL);
        if (params == null || (videoURL = params.getKey()) == null || (format = params.getValue()) == null) {
            throw new MalformedURLException(getInvalidVideoMessage());
        }
    }
}
