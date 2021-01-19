package org.example.downloader.hentaiplay;

import org.example.downloader.core.Downloader;
import org.example.downloader.core.WebScrapers;
import org.example.downloader.utils.JSoupDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class HentaiPlayDownloader extends Downloader {
    /**
     * Creates a HentaiPlayDownloader Object from the URL.
     *
     * @param hpURL the URL
     * @throws MalformedURLException the the exception that is thrown if the URL is no HentaiPlay site
     */
    public HentaiPlayDownloader(URL hpURL) throws MalformedURLException {
        super(hpURL);
        if (!HentaiPlayPage.isHentaiPlayPage(hpURL)) {
            throw new MalformedURLException("URL " + pageURL + " is no URL leading to a hentaiplay.net video");
        }
    }

    /**
     * Creates a HentaiPlayerDownloader Object from the String.
     *
     * @param hpURLString the String representing URL to HentaiPlay site
     * @throws MalformedURLException the exception that is thrown if the URL is no HentaiPlay site
     */
    public HentaiPlayDownloader(String hpURLString) throws MalformedURLException {
        super(hpURLString);
    }

    @Override
    protected URL generateVideoDownloadURL() {
        JSoupDriver driver = WebScrapers.noJavaScript();
        driver.get(pageURL.toString());

        List<WebElement> elements = driver.findElements(By.id("my-video"));
        for (WebElement element : elements) {
            WebElement source = element.findElement(By.tagName("source"));
            if (source != null && source.getAttribute("src").startsWith("https://hentaiplanet.info/")) {
                try {
                    return new URL(source.getAttribute("src"));
                } catch (MalformedURLException e) {
                    return null;
                }
            }
        }

        return null;
    }

    @Override
    public boolean needsJavaScript() {
        return false;
    }

    @Override
    public String getInvalidVideoMessage() {
        return "URL \"" + pageURL + "\" is no valid video on hentaiplay.net";
    }
}
