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
     * @return a Series providing multiple {@code Downloader}s
     * @throws MalformedURLException invalid URLs
     */
    Series provideSeries() throws MalformedURLException;
}
