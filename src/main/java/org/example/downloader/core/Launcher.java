package org.example.downloader.core;

import org.example.downloader.core.download.CollectiveDownloadBuilder;
import org.example.downloader.core.framework.Downloader;
import org.example.downloader.core.framework.Series;
import org.example.downloader.graphics.GUI;
import org.example.downloader.hentaiplay.HentaiPlayPage;
import org.example.downloader.utils.WebScrapers;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class Launcher {
    //TODO: animetoast.com -> Downloadwebsite
    //TODO: hentaiplay.net -> Hentais
    //TODO: hentaigasm.com -> HD Hentais
    //TODO: Adjust and implement (right now just a prototype)
    public static void main(String[] args) throws MalformedURLException {
        WebScrapers.initialize();
        if (!Arrays.asList(args).contains("nogui")) {
            GUI.start(args);
        } else {
            int maxDownloads = 4;
            if (args.length >= 2) {
                String path = args[0];
                String format = args[1];
                try {
                    maxDownloads = Integer.parseInt(args[2]);
                } catch (Exception ignored) {

                }
                Set<Downloader> downloaders = new LinkedHashSet<>();
                Set<Series> series = HentaiPlayPage.generateAllSeries();

                CollectiveDownloadBuilder downloadManager = new CollectiveDownloadBuilder("/home/timerertim/Downloads/", series);
                //downloadManager.execute(path, format, maxDownloads);
            }

            WebScrapers.close();
        }
    }
}
