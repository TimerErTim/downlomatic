package org.example.downloader.pages;

import org.example.downloader.core.framework.PageManager;
import org.example.downloader.pages.hentaigasm.HentaiGasmPage;
import org.example.downloader.pages.hentaiplay.HentaiPlayPage;

public enum Pages {
    HENTAIPLAY(new HentaiPlayPage()),
    HENTAIGASM(new HentaiGasmPage());

    private final PageManager page;

    Pages(PageManager page) {
        this.page = page;
    }

    /**
     * Returns the {@code PageManager} this Enum Constant
     * represents.
     *
     * @return the {@code PageManager}
     */
    public PageManager getManager() {
        return page;
    }
}
