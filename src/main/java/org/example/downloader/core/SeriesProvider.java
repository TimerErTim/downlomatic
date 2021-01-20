package org.example.downloader.core;

import java.net.MalformedURLException;

public interface SeriesProvider {
    /**
     * Provides a Series findable under a specified URL.
     * <p>
     * Managed by the implementing class. Therefore certain
     * links can cause a {@code MalformedURLException}
     * because the URL is not manageable with the according
     * implementation.
     * <p>
     * The series is only a collection of multiple {@link Download}s.
     * The download generation and handling has to be taken care of
     * by the implementing class. The max number of downloads is also
     * handled by the subclass.
     *
     * @param episodeFormat the formatting of each single episode in the Series
     * @return a Series providing
     * @throws MalformedURLException invalid URLs
     */
    Series provideSeries(String episodeFormat) throws MalformedURLException;
}
