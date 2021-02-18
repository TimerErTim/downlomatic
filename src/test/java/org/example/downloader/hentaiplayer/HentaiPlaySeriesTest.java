package org.example.downloader.hentaiplayer;

import org.example.downloader.core.download.Download;
import org.example.downloader.core.framework.Downloader;
import org.example.downloader.hentaiplay.HentaiPlaySeries;
import org.example.downloader.utils.WebScrapers;

import java.net.MalformedURLException;
import java.util.LinkedHashSet;
import java.util.Set;

public class HentaiPlaySeriesTest {
    public static void main(String[] args) throws MalformedURLException {
        WebScrapers.initialize();
        Set<Download> downloadPool = new LinkedHashSet<>();
        HentaiPlaySeries series = new HentaiPlaySeries("https://hentaiplay.net/episode-list/dropout/");
        series.fillDownloaders();
        for (Downloader downloader : series) {
            Download download = downloader.generateVideoDownload("/home/timerertim/Downloads/Dropout", "/S - /E");
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
