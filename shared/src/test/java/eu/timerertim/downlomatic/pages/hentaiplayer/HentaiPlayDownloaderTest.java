package eu.timerertim.downlomatic.pages.hentaiplayer;

import eu.timerertim.downlomatic.core.download.Download;
import eu.timerertim.downlomatic.pages.hentaiplay.HentaiPlayDownloader;
import eu.timerertim.downlomatic.utils.WebScrapers;

import java.io.IOException;

public class HentaiPlayDownloaderTest {
    public static void main(String[] args) throws IOException {
        HentaiPlayDownloader vivo = new HentaiPlayDownloader("https://hentaiplay.net/ane-yome-quartet-episode-1/");
        Download download = vivo.generateVideoDownload("/home/timerertim/Downloads/Test.mp4");
        download.startParallel();
        try {
            Thread.sleep(10000);
            download.pause();
            Thread.sleep(5000);
            download.startParallel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WebScrapers.close();
    }
}
