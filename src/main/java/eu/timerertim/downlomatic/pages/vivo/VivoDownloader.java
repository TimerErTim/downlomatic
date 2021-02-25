package eu.timerertim.downlomatic.pages.vivo;

import eu.timerertim.downlomatic.core.format.EpisodeFormat;
import eu.timerertim.downlomatic.core.format.EpisodeFormatBuilder;
import eu.timerertim.downlomatic.core.framework.Downloader;
import eu.timerertim.downlomatic.core.framework.Page;
import eu.timerertim.downlomatic.utils.WebScrapers;
import javafx.util.Pair;
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
    public Page getPage() {
        return VivoPage.PAGE;
    }

    @Override
    protected Pair<URL, EpisodeFormat> parseDownloader(URL pageURL) {
        EpisodeFormat format = new EpisodeFormatBuilder().build();
        URL url = null;

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
                    url = new URL(source);
                } else if ((source = video.getAttribute("src")) != null && source.startsWith("https://node--") && source.contains("vivo.sx")) {
                    url = new URL(source);
                }
            } catch (MalformedURLException e) {
                url = null;
            }
        }

        return new Pair<>(url, format);
    }

    @Override
    protected boolean isValidVideoURL(URL url) {
        return page.isValidPageURL(url) && url.toString().length() == "https://vivo.sx/".length() + 10;
    }
}
