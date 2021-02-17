package org.example.downloader.core.download;

public class CollectiveDownload {
    private long slowModeDelay;

    /**
     * Enables slow mode for this {@code CollectiveDownloadBuilder}.
     * <p>
     * The slow mode determines the minimum delay between fetching downloads
     * from the web rather than the download speed. This is useful
     * for websites with bot/DDoS protection.
     * <p>
     * The interval is one fetch every given amount of milliseconds.
     *
     * @param slowModeDelay the delay between each web request
     */
    public void setSlowModeDelay(long slowModeDelay) {
        if (slowModeDelay >= 0)
            this.slowModeDelay = slowModeDelay;
        else {
            this.slowModeDelay = 0;
        }
    }

    /**
     * Enables slow mode for this {@code CollectiveDownloadBuilder}.
     * <p>
     * The slow mode determines the minimum delay between fetching downloads
     * from the web rather than the download speed. This is useful
     * for websites with bot/DDoS protection.
     * <p>
     * The default interval is once every 500ms.
     */
    public void enableSlowMode() {
        if (this.slowModeDelay <= 0) {
            this.slowModeDelay = 500;
        }
    }

    /**
     * Disables slow mode for this {@code CollectiveDownloadBuilder}.
     * <p>
     * For more information consult {@link CollectiveDownloadBuilder#enableSlowMode()}.
     */
    public void disableSlowMode() {
        this.slowModeDelay = 0;
    }
}
