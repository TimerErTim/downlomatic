package eu.timerertim.downlomatic.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.logging.Level;

/**
 * Provides webscrapers for usage. Has to be initialized at least once in order to work.
 */
public class WebScrapers {
    private static JSoupDriver jSoupDriver;
    private static FirefoxDriver firefoxDriver;

    /**
     * Returns the WebDriver responsible for handling non js webscraping.
     *
     * @return the JSoupDriver
     */
    public static JSoupDriver noJavaScript() {
        return jSoupDriver;
    }

    /**
     * Returns the WebDriver responsible for handling JavaScript webscraping.
     *
     * @return the FirefoxDriver
     */
    public static WebDriver javaScript() {
        return firefoxDriver;
    }

    /**
     * If one of the WebDrivers happens to be closed, you can reinitialize them with this method.
     */
    public static void reInitialize() {
        if (firefoxDriver.toString().contains("null")) {
            initializeFirefoxDriver();
        }
        if (jSoupDriver.toString().contains("null")) {
            initializeJSoupDriver();
        }
    }

    /**
     * Closes both WebDrivers to release resources.
     */
    public static void close() {
        firefoxDriver.quit();
        jSoupDriver.quit();
    }

    /**
     * Initializes the WebScraper for handling (non-)JavaScript websites.
     */
    public static void initialize() {
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);

        WebDriverManager.firefoxdriver().setup();

        initializeFirefoxDriver();
        initializeJSoupDriver();
    }

    private static void initializeFirefoxDriver() {
        FirefoxOptions options = new FirefoxOptions().setHeadless(true).setLogLevel(FirefoxDriverLogLevel.FATAL).addArguments("--log", "fatal");
        options.setCapability("marionette", true);
        (firefoxDriver = new FirefoxDriver(options)).setLogLevel(Level.OFF);
    }

    private static void initializeJSoupDriver() {
        jSoupDriver = new JSoupDriver();
    }
}
