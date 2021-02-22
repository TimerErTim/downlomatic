package org.example.downloader.pages.vivo;

import org.example.downloader.core.framework.Page;

import java.net.URL;

public class VivoPage implements Page {
    public static final VivoPage PAGE = new VivoPage();

    @Override
    public boolean isValidPageURL(URL url) {
        return url.toString().startsWith("https://vivo.sx/");
    }

    @Override
    public String getPageDomain() {
        return "vivo.sx";
    }
}
