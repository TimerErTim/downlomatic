package org.example.downloader.pages.hentaigasm;

import org.example.downloader.core.framework.Downloader;
import org.example.downloader.core.framework.Page;
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

public class HentaiGasmSeries extends Series {
    /**
     * Creates a {@code HentaiGasmSeries} object from the URL.
     *
     * @param seriesURL the URL
     * @throws MalformedURLException the exception that is thrown if the URL is no valid HentaiGasm site
     */
    public HentaiGasmSeries(URL seriesURL) throws MalformedURLException {
        super(seriesURL);
    }

    /**
     * Creates a {@code HentaiGasmSeries} object from the URL.
     *
     * @param seriesURLString the String representing URL to HentaiGasm site
     * @throws MalformedURLException the exception that is thrown if the URL is no valid HentaiGasm site
     */
    public HentaiGasmSeries(String seriesURLString) throws MalformedURLException {
        super(seriesURLString);
    }

    /**
     * Creates a {@code HentaiGasmSeries} object from the URL and name.
     *
     * @param seriesURLString the String representing URL to HentaiGasm site
     * @param name            the name for graphical presentation of this {@code Series}
     * @throws MalformedURLException the exception that is thrown if the URL is no valid HentaiGasm site
     */
    public HentaiGasmSeries(String seriesURLString, String name) throws MalformedURLException {
        super(seriesURLString, name);
    }

    @Override
    public Page getPage() {
        return HentaiGasmPage.PAGE;
    }

    @Override
    protected Set<? extends Downloader> parseSeries(URL seriesURL) {
        Set<HentaiGasmDownloader> downloaders = new LinkedHashSet<>();

        JSoupDriver driver = WebScrapers.noJavaScript();
        driver.get(seriesURL.toString());

        // Parse Downloaders
        List<WebElement> elements = driver.findElements(By.tagName("a"));
        for (WebElement element : elements) {
            try {
                if (element.getAttribute("rel").equals("bookmark")) {
                    HentaiGasmDownloader downloader = new HentaiGasmDownloader(element.getAttribute("href"));
                    downloaders.add(downloader);
                }
            } catch (MalformedURLException ignored) {
            }
        }

        return downloaders;
    }

    @Override
    protected boolean isValidSeriesURL(URL seriesURL) {
        return page.isValidPageURL(seriesURL) && seriesURL.toString().contains(page.getPageDomain() + "/hentai/");
    }
}
