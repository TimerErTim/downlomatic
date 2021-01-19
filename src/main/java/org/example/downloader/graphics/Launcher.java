package org.example.downloader.graphics;

import org.example.downloader.core.WebScrapers;

import java.util.Arrays;

public class Launcher {
    //TODO: animetoast.com -> Downloadwebsite
    //TODO: hentaiplay.net -> Hentais
    public static void main(String[] args) {
        WebScrapers.initialize();
        if (!Arrays.asList(args).contains("nogui")) {
            GUI.start(args);
        } else {
            //TODO: Add args downloading
        }
    }
}
