package eu.timerertim.downlomatic.pages.vivo;

import eu.timerertim.downlomatic.core.framework.Page;

import java.net.URL;

public class VivoPage implements Page {
    public static final VivoPage PAGE = new VivoPage();

    @Override
    public boolean isValidPageURL(URL url) {
        return url.toString().startsWith("https://vivo.sx/");
    }

    @Override
    public String getPageDomain() {
        return "vivo.sx";
    }

    @Override
    public long getPageDelay() {
        return 0;
    }
}
