package org.example.downloader.graphics;

import org.example.downloader.core.WebScrapers;

import java.util.Arrays;

public class Launcher {
    //TODO: animetoast.com -> Downloadwebsite
    public static void main(String[] args) {
        WebScrapers.initialize();
        if (!Arrays.asList(args).contains("nogui")) {
            GUI.main(args);
        } else {
            //TODO: Add args downloading
        }
    }
}
