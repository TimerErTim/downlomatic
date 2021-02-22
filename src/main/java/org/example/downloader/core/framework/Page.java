package org.example.downloader.core.framework;

import java.net.URL;

public interface Page {
    /**
     * Validates the given {@code URL}.
     *
     * @param url the {@code URL} to be checked against
     * @return false if url is not leading to a valid page
     */
    boolean isValidPageURL(URL url);

    /**
     * Returns the domain name of the page.
     *
     * @return the domain name of the page
     */
    String getPageDomain();
}
