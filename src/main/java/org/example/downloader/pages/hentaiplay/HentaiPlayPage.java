package org.example.downloader.pages.hentaiplay;

import org.example.downloader.core.framework.PageManager;
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

public class HentaiPlayPage extends PageManager {
    private static Set<Series> allSeries;

    public static boolean isHentaiPlayPage(URL url) {
        return url.toString().startsWith("https://hentaiplay.net/");
    }

    /**
     * Returns every Series available on that website.
     * <p>
     * The {@code Set} of {@link Series} only contains "uninitialized"
     * {@code Series} objects.
     *
     * @return all Series objects fetchable from the webpage
     */
    public static Set<Series> generateAllSeries() {
        if (allSeries == null) {
            allSeries = new LinkedHashSet<>();

            JSoupDriver driver = WebScrapers.noJavaScript();
            driver.get("https://hentaiplay.net/hentai-index/");
            List<WebElement> seriesWrappers = driver.findElements(By.className("serieslist-content"));
            for (WebElement wrapper : seriesWrappers) {
                WebElement link = wrapper.findElement(By.tagName("a"));
                if (link != null) {
                    Series series;
                    try {
                        series = new HentaiPlaySeries(link.getAttribute("href"));
                    } catch (MalformedURLException e) {
                        series = null;
                    }
                    if (series != null) {
                        allSeries.add(series);
                    }
                }
            }
        }

        return allSeries;
    }

    public Series provideSeries() throws MalformedURLException {
        return null;
    }
}
