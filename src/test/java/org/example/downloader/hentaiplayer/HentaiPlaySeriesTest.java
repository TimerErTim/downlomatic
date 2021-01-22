package org.example.downloader.hentaiplayer;

import org.example.downloader.core.Download;
import org.example.downloader.core.Downloader;
import org.example.downloader.core.WebScrapers;
import org.example.downloader.hentaiplay.HentaiPlaySeries;

import java.net.MalformedURLException;
import java.util.LinkedHashSet;
import java.util.Set;

public class HentaiPlaySeriesTest {
    public static void main(String[] args) throws MalformedURLException {
        WebScrapers.initialize();
        Set<Download> downloadPool = new LinkedHashSet<>();
        HentaiPlaySeries series = new HentaiPlaySeries("https://hentaiplay.net/episode-list/dropout/");
        series.fillEpisodeDownloaders();
        int i = 1;
        for (Downloader downloader : series) {
            Download download = downloader.generateVideoDownload("/home/timerertim/Downloads/", "/S - /E", (bytes) -> {
            });
            synchronized (downloadPool) {
                downloadPool.add(download);
            }
            download.startParallel((success) -> {
                synchronized (downloadPool) {
                    downloadPool.remove(download);
                }
                System.out.println(download.toString() + " is finished");
            });
        }
    }
}
