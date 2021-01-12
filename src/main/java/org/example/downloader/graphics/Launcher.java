package org.example.downloader.graphics;

import java.util.Arrays;

public class Launcher {
    public static void main(String[] args) {
        if (!Arrays.asList(args).contains("nogui")) {
            GUI.main(args);
        } else {
            //TODO: Add args downloading
        }
    }
}
