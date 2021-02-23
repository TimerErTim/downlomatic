package org.example.downloader.pages;

import org.example.downloader.core.framework.Host;
import org.example.downloader.pages.hentaigasm.HentaiGasmPage;
import org.example.downloader.pages.hentaiplay.HentaiPlayPage;

/**
 * This enum works as "registry" for {@code Host}s.
 * <p>
 * Every {@link Host} has to be registered here by
 * creating an enum constant in order to be presented
 * to the user as available {@code Host} to choose from.
 */
public enum Hosts {
    HENTAIPLAY(HentaiPlayPage.PAGE, true),
    HENTAIGASM(HentaiGasmPage.PAGE, true);

    private final Host host;
    private final boolean nsfw;

    Hosts(Host page, boolean nsfw) {
        this.host = page;
        this.nsfw = nsfw;
    }

    /**
     * Returns the {@code Host} this enum constant
     * represents.
     *
     * @return the {@code Host}
     */
    public Host getHost() {
        return host;
    }

    /**
     * Returns if the {@code Host} this enum constant
     * represents is not safe for work.
     * <p>
     * Includes {@link Host}s which provide for example
     * hentai or porn.
     *
     * @return true if not safe for work
     */
    boolean isNsfw() {
        return nsfw;
    }
}
