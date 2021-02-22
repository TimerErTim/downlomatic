package org.example.downloader.core;

import org.example.downloader.core.download.CollectiveDownload;
import org.example.downloader.core.download.CollectiveDownloadBuilder;
import org.example.downloader.pages.hentaiplay.HentaiPlaySeries;
import org.example.downloader.utils.WebScrapers;

import java.net.MalformedURLException;


public class CollectiveDownloadTest {
    //TODO: Not working due to changes in collectiveDownload
    public static void main(String[] args) throws MalformedURLException, InterruptedException {
        WebScrapers.initialize();
        HentaiPlaySeries series = new HentaiPlaySeries("https://hentaiplay.net/episode-list/dropout/");

        CollectiveDownloadBuilder downloadManager = new CollectiveDownloadBuilder("/home/timerertim/Downloads/", series);
        CollectiveDownload download = downloadManager.execute();
        Thread.sleep(5000);
        download.pause();
        Thread.sleep(10000);
        download.download();
    }
}
