package org.example.downloader.core.framework;

import java.net.URL;

public interface Page {
    /**
     * Returns the delay in milliseconds that is needed when accessing
     * this {@code Page} to not trigger any kind of DDoS/bot protection
     * or similar.
     * <p>
     * The delay should be 0 to indicate that this {@code Page} needs no
     * delay between requests.
     *
     * @return the delay to not get blocked from {@code Page}
     */
    long getPageDelay();

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
