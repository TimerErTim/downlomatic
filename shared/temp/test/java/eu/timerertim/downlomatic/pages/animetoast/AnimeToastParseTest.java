package eu.timerertim.downlomatic.hosts.animetoast;

import eu.timerertim.downlomatic.util.JSoupDriver;
import eu.timerertim.downlomatic.util.WebScrapers;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

public class AnimeToastParseTest {
    public static void main(String[] args) {
        WebScrapers.initialize();
        JSoupDriver driver = WebScrapers.noJavaScript();
        WebDriver.Options options;
        (options = driver.manage()).addCookie(new Cookie("__cfduid", "dabcc6e6c53ff3a4a8be7505740191e531611065738"));
        options.addCookie(new Cookie("cf_clearance", "0951db3642734c8f43e514ae7d38bbd38b34ca74-1611062661-0-150"));
        options.addCookie(new Cookie("retina", "1"));
        driver.get("https://hentaiplay.net/", (doc) -> doc.followRedirects(true).
                header("Host", "hentaiplay.net").
                header("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:84.0) Gecko/20100101 Firefox/84.0").
                header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8").
                header("Accept-Language", "en-US,en;q=0.5").
                header("Accept-Encoding", "gzip, deflate, br").
                header("Referer", "https://www.google.com/").
                header("DNT", "1").
                header("Connection", "keep-alive").
                header("Upgrade-Insecure-Requests", "1").
                header("Cache-Control", "max-age=0").
                header("TE", "Trailers"));
        System.out.println(driver.getTitle());
        WebScrapers.close();
    }
}
