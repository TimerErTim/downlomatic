package org.example.downloader.pages.hentaiplay;

import javafx.util.Pair;
import org.example.downloader.core.format.EpisodeFormat;
import org.example.downloader.core.format.EpisodeFormatBuilder;
import org.example.downloader.core.framework.Downloader;
import org.example.downloader.core.framework.Page;
import org.example.downloader.utils.JSoupDriver;
import org.example.downloader.utils.WebScrapers;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class HentaiPlayDownloader extends Downloader {
    /**
     * Creates a {@code HentaiPlayDownloader} object from the URL.
     *
     * @param hpURL the URL
     * @throws MalformedURLException the exception that is thrown if the URL is no HentaiPlay site
     */
    public HentaiPlayDownloader(URL hpURL) throws MalformedURLException {
        super(hpURL);
    }

    /**
     * Creates a {@code HentaiPlayDownloader} object from the String.
     *
     * @param hpURLString the String representing URL to HentaiPlay site
     * @throws MalformedURLException the exception that is thrown if the URL is no HentaiPlay site
     */
    public HentaiPlayDownloader(String hpURLString) throws MalformedURLException {
        super(hpURLString);
    }

    @Override
    public Page getPage() {
        return HentaiPlayPage.PAGE;
    }

    @Override
    protected Pair<URL, EpisodeFormat> parseDownloader(URL pageURL) {
        EpisodeFormat format;
        URL url = null;

        JSoupDriver driver = WebScrapers.noJavaScript();
        driver.get(pageURL.toString());

        // Parse EpisodeFormat
        EpisodeFormatBuilder builder = new EpisodeFormatBuilder();
        WebElement title = driver.findElement(By.className("entry-title"));
        String[] temp = title.getText().split(" Episode ");
        builder.setSeriesName(temp[0]);
        temp = temp[1].split(" ");
        builder.setEpisodeNumber(temp[0]);
        if (temp.length > 1 && temp[1].equals("English")) {
            builder.setLanguage(temp[1]);
            builder.setTranslationType("Sub");
        } else {
            builder.setLanguage("Japanese");
        }
        format = builder.build();

        // Parse Video URL
        List<WebElement> elements = driver.findElements(By.id("my-video"));
        elements.addAll(driver.findElements(By.id("my_video_1")));
        for (WebElement element : elements) {
            WebElement source = element.findElement(By.tagName("source"));
            if (source != null && (source.getAttribute("src").startsWith("https://hentaiplanet.info/") || source.getAttribute("src").startsWith("https://openload.co/embed/"))) {
                try {
                    url = new URL(source.getAttribute("src"));
                    break;
                } catch (MalformedURLException ignored) {
                }
            }
        }

        return new Pair<>(url, format);
    }

    @Override
    protected boolean isValidVideoURL(URL url) {
        return page.isValidPageURL(url);
    }
}
