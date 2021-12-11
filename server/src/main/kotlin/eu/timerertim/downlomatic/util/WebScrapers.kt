package eu.timerertim.downlomatic.util

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxDriverLogLevel
import org.openqa.selenium.firefox.FirefoxOptions
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Provides webscrapers for usage. Has to be initialized at least once in order to work.
 */
object WebScrapers {
    private lateinit var jSoupDriver: JSoupDriver
    private lateinit var firefoxDriver: FirefoxDriver

    /**
     * Returns the WebDriver responsible for handling non js webscraping.
     *
     * @return the JSoupDriver
     */
    @JvmStatic
    fun noJavaScript(): JSoupDriver {
        if (this::jSoupDriver.isInitialized.not()) {
            initializeJSoupDriver()
        }
        return jSoupDriver
    }

    /**
     * Returns the WebDriver responsible for handling JavaScript webscraping.
     *
     * @return the FirefoxDriver
     */
    @JvmStatic
    fun javaScript(): WebDriver {
        if (this::firefoxDriver.isInitialized.not()) {
            initializeFirefoxDriver()
        }
        return firefoxDriver
    }

    /**
     * If one of the WebDrivers happens to be closed, you can reinitialize them with this method.
     */
    fun reInitialize() {
        if (firefoxDriver.toString().contains("null")) {
            initializeFirefoxDriver()
        }
        if (jSoupDriver.toString().contains("null")) {
            initializeJSoupDriver()
        }
    }

    /**
     * Closes both WebDrivers to release resources.
     */
    @JvmStatic
    fun close() {
        if (this::firefoxDriver.isInitialized) firefoxDriver.quit()
        if (this::jSoupDriver.isInitialized) jSoupDriver.quit()
    }

    /**
     * Initializes the WebScraper for handling (non-)JavaScript websites.
     */
    @JvmStatic
    fun initialize() {
        initializeFirefoxDriver()
        initializeJSoupDriver()
    }

    private fun initializeFirefoxDriver() {
        Logger.getLogger("org.apache.hc.client5.http").level = Level.OFF
        Logger.getLogger("org.apache.logging").level = Level.OFF
        WebDriverManager.firefoxdriver().setup()

        Logger.getLogger("org.openqa.selenium").level = Level.OFF
        val options =
            FirefoxOptions().setHeadless(true).setLogLevel(FirefoxDriverLogLevel.FATAL).addArguments("--log", "fatal")
        options.setCapability("marionette", true)
        FirefoxDriver(options).also { firefoxDriver = it }.setLogLevel(Level.OFF)
    }

    private fun initializeJSoupDriver() {
        jSoupDriver = JSoupDriver()
    }
}