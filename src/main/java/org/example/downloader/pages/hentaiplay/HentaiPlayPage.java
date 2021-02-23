package org.example.downloader.pages.hentaiplay;

import org.example.downloader.core.framework.Downloader;
import org.example.downloader.core.framework.Host;
import org.example.downloader.core.framework.Series;
import org.example.downloader.utils.JSoupDriver;
import org.example.downloader.utils.WebScrapers;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class HentaiPlayPage extends Host {
    public static final HentaiPlayPage PAGE = new HentaiPlayPage();

    @Override
    public Series getSeries(URL seriesURL) throws MalformedURLException {
        return new HentaiPlaySeries(seriesURL);
    }

    @Override
    public Downloader getDownloader(URL videoURL) throws MalformedURLException {
        return new HentaiPlayDownloader(videoURL);
    }

    @Override
    public boolean isValidPageURL(URL url) {
        return url.toString().startsWith("https://hentaiplay.net/");
    }

    @Override
    public String getPageDomain() {
        return "hentaiplay.net";
    }

    @Override
    protected URL getListURL() throws MalformedURLException {
        return new URL("https://hentaiplay.net/hentai-index/");
    }

    @Override
    protected Set<Series> parsePage(URL listURL) {
        Set<Series> parsedSeries = new LinkedHashSet<>();

        JSoupDriver driver = WebScrapers.noJavaScript();
        driver.get(listURL.toString());

        // Parse Series
        List<WebElement> seriesWrappers = driver.findElements(By.className("serieslist-content"));
        for (WebElement wrapper : seriesWrappers) {
            WebElement link = wrapper.findElement(By.tagName("a"));
            if (link != null) {
                try {
                    Series series = new HentaiPlaySeries(link.getAttribute("href"), link.getAttribute("title"));
                    parsedSeries.add(series);
                } catch (MalformedURLException ignored) {
                }
            }
        }

        return parsedSeries;
    }
}
