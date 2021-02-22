package org.example.downloader.pages.hentaiplay;

import org.example.downloader.core.format.EpisodeFormat;
import org.example.downloader.core.format.EpisodeFormatBuilder;
import org.example.downloader.core.framework.Downloader;
import org.example.downloader.utils.JSoupDriver;
import org.example.downloader.utils.WebScrapers;
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
    protected EpisodeFormat generateEpisodeFormatNotSetting() {
        JSoupDriver driver = WebScrapers.noJavaScript();
        driver.get(pageURL.toString());
        WebElement title = driver.findElement(By.className("entry-title"));
        EpisodeFormatBuilder formatGenerator = new EpisodeFormatBuilder();
        String[] temp = title.getText().split(" Episode ");
        String seriesName = temp[0];
        temp = temp[1].split(" ");
        String episodeNumber = temp[0];
        String seasonNumber = temp[1];
        String episodeName = "Episode " + episodeNumber;

        return new EpisodeFormatBuilder().setEpisodeName(episodeName).setEpisodeNumber(episodeNumber).setSeriesName(seriesName).generate();
    }

    @Override
    protected URL generateVideoDownloadURL() {
        JSoupDriver driver = WebScrapers.noJavaScript();
        driver.get(pageURL.toString());

        List<WebElement> elements = driver.findElements(By.id("my-video"));
        elements.addAll(driver.findElements(By.id("my_video_1")));
        for (WebElement element : elements) {
            WebElement source = element.findElement(By.tagName("source"));
            if (source != null && (source.getAttribute("src").startsWith("https://hentaiplanet.info/") || source.getAttribute("src").startsWith("https://openload.co/embed/"))) {
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
    protected String getInvalidVideoMessage() {
        return "URL \"" + pageURL + "\" is no valid video on hentaiplay.net";
    }
}
