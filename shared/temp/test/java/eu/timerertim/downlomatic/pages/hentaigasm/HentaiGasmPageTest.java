package eu.timerertim.downlomatic.hosts.hentaigasm;

import eu.timerertim.downlomatic.core.framework.Host;
import eu.timerertim.downlomatic.core.framework.Series;
import eu.timerertim.downlomatic.util.Utils;

import java.net.MalformedURLException;
import java.util.Set;

public class HentaiGasmPageTest {
    public static void main(String... args) throws MalformedURLException {
        Utils.initializeSetup();
        Host host = new HentaiGasmPage();
        Set<Series> series = host.getSeries();
        for (Series serie : series) {
            System.out.println(serie.getName());
        }
    }
}
