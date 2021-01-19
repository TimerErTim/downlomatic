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
     *
     * @return a Series providing
     * @throws MalformedURLException invalid URLs
     */
    Series provideSeries() throws MalformedURLException;
}
