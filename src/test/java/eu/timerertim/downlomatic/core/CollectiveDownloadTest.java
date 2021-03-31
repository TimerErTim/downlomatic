package eu.timerertim.downlomatic.core;

import eu.timerertim.downlomatic.core.download.CollectiveDownload;
import eu.timerertim.downlomatic.core.download.CollectiveDownloadBuilder;
import eu.timerertim.downlomatic.pages.hentaiplay.HentaiPlaySeries;

import java.net.MalformedURLException;


public class CollectiveDownloadTest {
    public static void main(String[] args) throws MalformedURLException, InterruptedException {
        HentaiPlaySeries series = new HentaiPlaySeries("https://hentaiplay.net/episode-list/dropout/");

        CollectiveDownloadBuilder downloadManager = new CollectiveDownloadBuilder("/home/timerertim/Downloads/", series);
        CollectiveDownload download = downloadManager.execute();
        Thread.sleep(5000);
        download.pause();
        Thread.sleep(10000);
        download.download();
    }
}
