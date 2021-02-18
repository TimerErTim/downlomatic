package org.example.downloader.animetoast;

import org.example.downloader.core.framework.Downloader;
import org.example.downloader.core.framework.Series;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

public class AnimeToastSeries extends Series {
    //TODO: Implement
    public AnimeToastSeries(URL seriesURL) throws MalformedURLException {
        super(seriesURL);
    }

    public AnimeToastSeries(String seriesURLString) throws MalformedURLException {
        super(seriesURLString);
    }

    @Override
    protected Set<? extends Downloader> generateEpisodeDownloaders() {
        return null;
    }

    @Override
    public String getInvalidSeriesMessage() {
        return null;
    }
}
