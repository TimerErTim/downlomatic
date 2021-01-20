package org.example.downloader.hentaiplay;

import org.example.downloader.core.PageManager;
import org.example.downloader.core.Series;
import org.example.downloader.core.SeriesProvider;

import java.net.MalformedURLException;
import java.net.URL;

public class HentaiPlayPage extends PageManager implements SeriesProvider {
    public static boolean isHentaiPlayPage(URL url) {
        return url.toString().startsWith("https://hentaiplay.net/");
    }

    @Override
    public Series provideSeries(String episodeFormat) throws MalformedURLException {
        return null;
    }
}
