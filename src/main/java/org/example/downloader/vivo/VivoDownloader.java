package org.example.downloader.vivo;

import org.example.downloader.core.Download;
import org.example.downloader.core.WebScrapers;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.function.IntConsumer;

public class VivoDownloader {
    private final URL vivoURL;
    private Download download;
    private URL videoURL;

    /**
     * Creates a VivoDownloader Object from the URL.
     *
     * @param vivoURL the URL
     * @throws MalformedURLException the the exception that is thrown if the URL is no Vivo site
     */
    public VivoDownloader(URL vivoURL) throws MalformedURLException {
        this.vivoURL = vivoURL;
        if (!(vivoURL.toString().startsWith("https://vivo.sx/") && vivoURL.toString().length() == "https://vivo.sx/".length() + 10)) {
            throw new MalformedURLException("URL \"" + vivoURL + "\" is no URL leading to a vivo.sx video");
        }
    }

    /**
     * Creates a VivoDownloader Object from the String.
     *
     * @param vivoURLString the String representing URL to Vivo site
     * @throws MalformedURLException the exception that is thrown if the URL is no Vivo site
     */
    public VivoDownloader(String vivoURLString) throws MalformedURLException {
        this(new URL(vivoURLString));
    }

    /**
     * Generates a Download Object, which can be used to download the video.
     * <p>
     * Returns null if any complications occur (vivo link without a valid video,
     * URL not openable, etc.).
     * <p>
     * onRead makes it possible to compute and keep track of progress, because
     * everytime a new buffer of bytes is read, onRead is called with the current
     * amount of bytes downloaded as parameter.
     *
     * @param fileString the path to the storing file - is automatically created
     * @param onRead     the action on byte reads
     * @return a Download Object representing the video download.
     * @throws MalformedURLException the exception that is thrown if the URL is no valid Vivo video
     */
    public Download generateVideoDownload(String fileString, IntConsumer onRead) throws MalformedURLException {
        try {
            if ((videoURL = generateVideoDownloadURL()) == null) {
                throw new MalformedURLException("URL \"" + vivoURL + "\" is no valid video on vivo.sx");
            }

            download = new Download(videoURL, new File(fileString), onRead);
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
     * Returns null if any complications occur (vivo link without a valid video,
     * URL not openable, etc.).
     *
     * @param fileString the path to the storing file - is automatically created
     * @return a Download Object representing the video download.
     * @throws MalformedURLException the exception that is thrown if the URL is no valid Vivo video
     */
    public Download generateVideoDownload(String fileString) throws MalformedURLException {
        return generateVideoDownload(fileString, (r) -> {
        });
    }

    private URL generateVideoDownloadURL() {
        WebDriver driver = WebScrapers.javaScript();
        driver.get(vivoURL.toString());

        final List<WebElement> videoNodes = driver.findElements(By.tagName("video"));
        for (WebElement video : videoNodes) {
            String source;
            try {
                source = video.findElement(By.tagName("source")).getAttribute("src");
            } catch (NoSuchElementException ex) {
                source = null;
            }

            try {
                if (source != null && source.startsWith("https://node--") && source.contains("vivo.sx")) {
                    return new URL(source);
                } else if ((source = video.getAttribute("src")) != null && source.startsWith("https://node--") && source.contains("vivo.sx")) {
                    return new URL(source);
                }
            } catch (MalformedURLException e) {
                return null;
            }
        }

        return null;
    }
}
