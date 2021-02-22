package org.example.downloader.pages.hentaigasm;

import org.example.downloader.core.framework.Host;

import java.net.URL;

public class HentaiGasmPage extends Host {
    public final static HentaiGasmPage PAGE = new HentaiGasmPage();

    @Override
    public boolean isValidPageURL(URL url) {
        return url.toString().startsWith("http://hentaigasm.com/");
    }

    @Override
    public String getPageDomain() {
        return "hentaigasm.com";
    }
}
