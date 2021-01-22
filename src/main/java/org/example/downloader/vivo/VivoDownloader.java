package org.example.downloader.vivo;

import org.example.downloader.core.Downloader;
import org.example.downloader.core.EpisodeFormat;
import org.example.downloader.core.WebScrapers;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class VivoDownloader extends Downloader {
    /**
     * Creates a VivoDownloader Object from the URL.
     *
     * @param pageURL the URL
     * @throws MalformedURLException the the exception that is thrown if the URL is no Vivo site
     */
    public VivoDownloader(URL pageURL) throws MalformedURLException {
        super(pageURL);
        if (!(pageURL.toString().startsWith("https://vivo.sx/") && pageURL.toString().length() == "https://vivo.sx/".length() + 10)) {
            throw new MalformedURLException("URL \"" + pageURL + "\" is no URL leading to a vivo.sx video");
        }
    }

    /**
     * Creates a VivoDownloader Object from the String.
     *
     * @param vivoURLString the String representing URL to Vivo site
     * @throws MalformedURLException the exception that is thrown if the URL is no Vivo site
     */
    public VivoDownloader(String vivoURLString) throws MalformedURLException {
        super(vivoURLString);
    }

    @Override
    protected EpisodeFormat generateEpisodeFormatNotSetting() {
        return null;
    }

    @Override
    protected URL generateVideoDownloadURL() {
        WebDriver driver = WebScrapers.javaScript();
        driver.get(pageURL.toString());

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

    @Override
    public boolean needsJavaScript() {
        return true;
    }

    @Override
    protected String getInvalidVideoMessage() {
        return "URL \"" + pageURL + "\" is no valid video on vivo.sx";
    }
}
