package org.example.downloader.core;

import org.example.downloader.hentaiplay.HentaiPlaySeries;

import java.net.MalformedURLException;
import java.util.LinkedHashSet;
import java.util.Set;

public class CollectiveDownloadTest {
    public static void main(String[] args) throws MalformedURLException {
        WebScrapers.initialize();
        HentaiPlaySeries series = new HentaiPlaySeries("https://hentaiplay.net/episode-list/dropout/");
        series.fillDownloaders();
        Set<Downloader> downloaderSet = new LinkedHashSet<>();
        for (Downloader downloader : series) {
            downloaderSet.add(downloader);
        }

        CollectiveDownload downloadManager = new CollectiveDownload(downloaderSet);
        downloadManager.execute("/home/timerertim/Downloads/", "/S - /E", 2);
    }
}
