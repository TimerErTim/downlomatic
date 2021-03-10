package eu.timerertim.downlomatic.pages.hentaigasm;

import eu.timerertim.downlomatic.core.download.Download;
import eu.timerertim.downlomatic.core.format.EpisodeFormat;
import eu.timerertim.downlomatic.core.format.EpisodeFormatBuilder;
import eu.timerertim.downlomatic.core.framework.Downloader;
import eu.timerertim.downlomatic.core.framework.Page;
import eu.timerertim.downlomatic.utils.JSoupDriver;
import eu.timerertim.downlomatic.utils.WebScrapers;
import javafx.util.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * Represents a single video on hentaigasm and is used to retrieve a {@link Download}.
 */
public class HentaiGasmDownloader extends Downloader {
    /**
     * Creates a {@code HentaiGasmDownloader} object from the URL.
     *
     * @param pageURL the URL
     * @throws MalformedURLException the exception that is thrown if the URL is no HentaiGasm site
     */
    public HentaiGasmDownloader(URL pageURL) throws MalformedURLException {
        super(pageURL);

    }

    /**
     * Creates a {@code HentaiGasmDownloader} object from the URL.
     *
     * @param pageURLString the String representing URL to HentaiGasm site
     * @throws MalformedURLException the exception that is thrown if the URL is no HentaiGasm site
     */
    public HentaiGasmDownloader(String pageURLString) throws MalformedURLException {
        super(pageURLString);
    }

    @Override
    public Page getPage() {
        return HentaiGasmPage.PAGE;
    }

    @Override
    protected Pair<URL, EpisodeFormat> parseDownloader(URL pageURL) {
        EpisodeFormat format;
        URL url = null;

        JSoupDriver driver = WebScrapers.noJavaScript();
        driver.get(pageURL.toString());

        // Parse EpisodeFormat
        EpisodeFormatBuilder builder = new EpisodeFormatBuilder();
        WebElement element = driver.findElement(By.id("title"));
        String title = element.getText();
        String seriesName = title.replaceAll("\\d+ (Subbed|Raw)", "");
        String[] postSeriesName = title.replaceAll(Pattern.quote(seriesName), "").split(" ");
        builder.setEpisodeNumber(postSeriesName[0]);
        builder.setLanguage(postSeriesName[1].equalsIgnoreCase("Subbed") ? "English" : "Japanese");
        builder.setTranslationType(postSeriesName[1].equalsIgnoreCase("Subbed") ? "Sub" : null);
        builder.setSeriesName(seriesName.substring(0, seriesName.length() - 1));
        format = builder.build();

        // Parse Video URL
        element = driver.findElement(By.className("jw-video"));
        try {
            url = new URL(element.getAttribute("src"));
        } catch (Exception ignored) {
        }

        return new Pair<>(url, format);
    }

    @Override
    protected boolean isValidVideoURL(URL url) {
        return page.isValidPageURL(url);
    }
}
