package org.example.downloader.core;

import org.example.downloader.core.download.CollectiveDownloadBuilder;
import org.example.downloader.hentaiplay.HentaiPlaySeries;
import org.example.downloader.utils.WebScrapers;

import java.net.MalformedURLException;


public class CollectiveDownloadTest {
    //TODO: Not working due to changes in collectiveDownload
    public static void main(String[] args) throws MalformedURLException {
        WebScrapers.initialize();
        HentaiPlaySeries series = new HentaiPlaySeries("https://hentaiplay.net/episode-list/dropout/");

        CollectiveDownloadBuilder downloadManager = new CollectiveDownloadBuilder("/home/timerertim/Downloads/", series);
        downloadManager.execute();
    }
}
