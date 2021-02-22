package org.example.downloader.pages.animetoast;

import javafx.util.Pair;
import org.example.downloader.core.framework.Downloader;
import org.example.downloader.core.framework.Page;
import org.example.downloader.core.framework.Series;

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
    protected Pair<Set<? extends Downloader>, String> parseSeries(URL seriesURL) {
        return null;
    }

    @Override
    protected boolean isValidSeriesURL(URL seriesURL) {
        return false;
    }

    @Override
    protected Page getPage() {
        return null;
    }
}
