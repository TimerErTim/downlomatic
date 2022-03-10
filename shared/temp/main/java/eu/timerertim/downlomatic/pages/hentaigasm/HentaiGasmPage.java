package eu.timerertim.downlomatic.pages.hentaigasm;

import eu.timerertim.downlomatic.core.framework.Downloader;
import eu.timerertim.downlomatic.core.framework.Host;
import eu.timerertim.downlomatic.core.framework.Series;
import eu.timerertim.downlomatic.utils.JSoupDriver;
import eu.timerertim.downlomatic.utils.WebScrapers;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Defines the interaction with and values for hentaigasm.
 */
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
                driver.get(listURL + "page/" + page + "/");
            }

            List<WebElement> elements = driver.findElements(By.tagName("a"));

            for (WebElement element : elements) {
                if ("bookmark".equals(element.getAttribute("rel"))) {
                    String link = element.getAttribute("href");
                    String title = element.getText();
                    String seriesName = title.replaceAll(" \\d+ (Subbed|Raw)", "");

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

                    @Override
                    protected boolean isValidSeriesURL(URL seriesURL) {
                        return true;
                    }
                };
                parsedSeries.add(series);
            } catch (MalformedURLException ignored) {
            }
        }

        return parsedSeries;
    }
}
