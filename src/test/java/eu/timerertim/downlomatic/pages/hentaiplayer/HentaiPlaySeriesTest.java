package eu.timerertim.downlomatic.pages.hentaiplayer;

import eu.timerertim.downlomatic.core.download.Download;
import eu.timerertim.downlomatic.core.framework.Downloader;
import eu.timerertim.downlomatic.pages.hentaiplay.HentaiPlaySeries;
import eu.timerertim.downlomatic.utils.WebScrapers;

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
