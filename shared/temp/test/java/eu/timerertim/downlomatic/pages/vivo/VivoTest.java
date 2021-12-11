package eu.timerertim.downlomatic.hosts.vivo;

import eu.timerertim.downlomatic.core.download.Download;
import eu.timerertim.downlomatic.util.WebScrapers;

import java.io.IOException;

public class VivoTest {
    public static void main(String[] args) throws IOException {
        WebScrapers.initialize();
        VivoDownloader vivo = new VivoDownloader("https://vivo.sx/a4208d24f7");
        Download download = vivo.generateVideoDownload("/home/timerertim/Downloads/Test.mp4");
        download.startDownload();
    }
}
