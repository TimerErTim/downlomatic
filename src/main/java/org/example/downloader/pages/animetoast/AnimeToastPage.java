package org.example.downloader.pages.animetoast;

import org.example.downloader.core.framework.Downloader;
import org.example.downloader.core.framework.Host;
import org.example.downloader.core.framework.Series;

import java.net.URL;
import java.util.Set;

public class AnimeToastPage extends Host {
    public final static AnimeToastPage PAGE = new AnimeToastPage();

    @Override
    public boolean isValidPageURL(URL url) {
        return false;
    }

    @Override
    public String getPageDomain() {
        return "animetoast.com";
    }

    @Override
    protected Set<Series> parsePage(URL listURL) {
        return null;
    }

    @Override
    protected URL getListURL() {
        return null;
    }

    @Override
    public Series getSeries(URL seriesURL) {
        return null;
    }

    @Override
    public Downloader getDownloader(URL videoURL) {
        return null;
    }
}
