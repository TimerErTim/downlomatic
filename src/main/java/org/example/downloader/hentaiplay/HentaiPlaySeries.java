package org.example.downloader.hentaiplay;

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

public class HentaiPlaySeries extends Series {

    /**
     * Creates a HentaiPlaySeries Object from the URL.
     *
     * @param seriesURL the URL
     * @throws MalformedURLException the the exception that is thrown if the URL is no valid HentaiPlay site
     */
    public HentaiPlaySeries(URL seriesURL) throws MalformedURLException {
        super(seriesURL);
        if (!(HentaiPlayPage.isHentaiPlayPage(seriesURL) && seriesURL.toString().contains("/episode-list/"))) {
            throw new MalformedURLException("URL " + seriesURL + " is no URL leading to a hentaiplay.net episode list");
        }
    }

    /**
     * Creates a HentaiPlaySeries Object from the String.
     *
     * @param seriesURLString the String representing URL to HentaiPlay site
     * @throws MalformedURLException the exception that is thrown if the URL is no HentaiPlay site
     */
    public HentaiPlaySeries(String seriesURLString) throws MalformedURLException {
        super(seriesURLString);
    }

    @Override
    protected Set<HentaiPlayDownloader> generateEpisodeDownloaders() {
        Set<HentaiPlayDownloader> downloaders = new LinkedHashSet<>();
        JSoupDriver driver = WebScrapers.noJavaScript();

        driver.get(seriesURL.toString());
        List<WebElement> elements = driver.findElements(By.className("category-episodes"));
        for (WebElement element : elements) {
            try {
                HentaiPlayDownloader downloader = new HentaiPlayDownloader(element.findElement(By.className("clip-link")).getAttribute("href"));
                downloaders.add(downloader);
            } catch (MalformedURLException ignored) {

            }
        }

        return downloaders;
    }

    @Override
    public String getInvalidSeriesMessage() {
        return "URL \"" + seriesURL + "\" is no valid series on hentaiplay.net";
    }
}
