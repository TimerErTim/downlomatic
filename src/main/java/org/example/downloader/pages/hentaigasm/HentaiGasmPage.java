package org.example.downloader.pages.hentaigasm;

import org.example.downloader.core.framework.Downloader;
import org.example.downloader.core.framework.Host;
import org.example.downloader.core.framework.Series;
import org.example.downloader.utils.JSoupDriver;
import org.example.downloader.utils.WebScrapers;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class HentaiGasmPage extends Host {
    public final static HentaiGasmPage PAGE = new HentaiGasmPage();

    @Override
    public Series getSeries(URL seriesURL) throws MalformedURLException {
        return new HentaiGasmSeries(seriesURL);
    }

    @Override
    public Downloader getDownloader(URL videoURL) throws MalformedURLException {
        return new HentaiGasmDownloader(videoURL);
    }

    @Override
    public long getPageDelay() {
        return 0;
    }

    @Override
    public boolean isValidPageURL(URL url) {
        return url.toString().startsWith("http://hentaigasm.com/");
    }

    @Override
    public String getPageDomain() {
        return "hentaigasm.com";
    }

    @Override
    protected URL getListURL() throws MalformedURLException {
        return new URL("http://hentaigasm.com/");
    }

    @Override
    protected Set<Series> parsePage(URL listURL) {
        Set<Series> parsedSeries = new LinkedHashSet<>();
        HashMap<String, Set<String>> map = new HashMap<>();
        JSoupDriver driver = WebScrapers.noJavaScript();

        // Parse Series
        boolean found = true;
        for (int page = 1; found; page++) {
            found = false;
            if (page == 1) {
                driver.get(listURL.toString());
            } else {
                driver.get(listURL.toString() + "page/" + page + "/");
            }

            List<WebElement> elements = driver.findElements(By.tagName("a"));
            for (WebElement element : elements) {
                if (element.getAttribute("rel").equals("bookmark")) {
                    String link = element.getAttribute("href");
                    String title = element.getText();
                    String seriesName = title.replaceAll("\\d+ (Subbed|Raw)", "");

                    Set<String> links = map.computeIfAbsent(seriesName, k -> new LinkedHashSet<>());
                    links.add(link);

                    found = true;
                }
            }
        }

        map.remove("UNCENSORED 1 SUBBED");
        for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
            Set<Downloader> downloaders = entry.getValue().stream().
                    map((link) -> {
                        try {
                            return new HentaiGasmDownloader(link);
                        } catch (MalformedURLException e) {
                            return null;
                        }
                    }).collect(Collectors.toCollection(LinkedHashSet::new));
            try {
                Series series = new HentaiGasmSeries(getListURL().toString(), entry.getKey()) {
                    @Override
                    protected Set<? extends Downloader> parseSeries(URL seriesURL) {
                        return downloaders;
                    }
                };
                parsedSeries.add(series);
            } catch (MalformedURLException ignored) {
            }
        }

        return parsedSeries;
    }
}
