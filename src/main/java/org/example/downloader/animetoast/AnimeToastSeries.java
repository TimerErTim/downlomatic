package org.example.downloader.animetoast;

import org.example.downloader.core.Downloader;
import org.example.downloader.core.Series;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

public class AnimeToastSeries extends Series {
    //TODO: Implement
    protected AnimeToastSeries(URL seriesURL) throws MalformedURLException {
        super(seriesURL);
    }

    protected AnimeToastSeries(String seriesURLString) throws MalformedURLException {
        super(seriesURLString);
    }

    @Override
    protected Set<? extends Downloader> generateEpisodeDownloaders() {
        return null;
    }

    @Override
    public boolean needsJavaScript() {
        return false;
    }

    @Override
    public String getInvalidSeriesMessage() {
        return null;
    }
}
