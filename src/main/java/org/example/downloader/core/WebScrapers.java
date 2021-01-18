package org.example.downloader.core;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.example.downloader.utils.JSoupDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.logging.Level;

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
            FirefoxOptions options = new FirefoxOptions().setHeadless(true).setLogLevel(FirefoxDriverLogLevel.FATAL).addArguments("--log", "fatal");
            options.setCapability("marionette", true);
            (firefoxDriver = new FirefoxDriver(options)).setLogLevel(Level.OFF);
        }
        if (jSoupDriver.toString().contains("null")) {
            jSoupDriver = new JSoupDriver();
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
    public void initialize() {
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);

        WebDriverManager.firefoxdriver().setup();

        FirefoxOptions options = new FirefoxOptions().setHeadless(true).setLogLevel(FirefoxDriverLogLevel.FATAL).addArguments("--log", "fatal");
        options.setCapability("marionette", true);
        (firefoxDriver = new FirefoxDriver(options)).setLogLevel(Level.OFF);
        jSoupDriver = new JSoupDriver();
    }
}
