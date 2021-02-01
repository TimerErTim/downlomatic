package org.example.downloader.core;

import org.example.downloader.graphics.GUI;
import org.example.downloader.hentaiplay.HentaiPlayPage;

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
                int i = 1;
                for (Series seriesSingle : series) {
                    seriesSingle.fillEpisodeDownloaders();
                    System.out.print("\r" + i++ + "/" + series.size() + " Series");
                    for (Downloader downloader : seriesSingle) {
                        downloaders.add(downloader);
                    }
                }
                System.out.println();

                CollectiveDownload downloadManager = new CollectiveDownload(downloaders);
                downloadManager.execute(path, format, maxDownloads);
            }

            WebScrapers.close();
        }
    }
}
