package org.example.downloader.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public abstract class Series {
    //TODO: Implement
    protected final URL seriesURL;
    private List<Downloader> source;

    protected Series(URL seriesURL) throws MalformedURLException {
        this.seriesURL = seriesURL;
    }

    protected Series(String seriesURLString) throws MalformedURLException {
        this(new URL(seriesURLString));
    }
}
