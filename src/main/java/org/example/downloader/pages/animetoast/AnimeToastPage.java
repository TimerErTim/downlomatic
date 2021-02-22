package org.example.downloader.pages.animetoast;

import org.example.downloader.core.framework.Host;

import java.net.URL;

public class AnimeToastPage extends Host {

    @Override
    public boolean isValidPageURL(URL url) {
        return false;
    }

    @Override
    public String getPageDomain() {
        return "animetoast.com";
    }
}
