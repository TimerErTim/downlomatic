package eu.timerertim.downlomatic.utils

import eu.timerertim.downlomatic.utils.logging.Log.f
import eu.timerertim.downlomatic.utils.logging.Log.w
import org.jsoup.Connection
import org.jsoup.helper.HttpConnection
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.openqa.selenium.*
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebDriver.*
import org.openqa.selenium.internal.*
import org.openqa.selenium.logging.Logs
import java.io.IOException
import java.net.URL
import java.util.*
import java.util.stream.Collectors

/**
 * JSoup based implementation of Selenium's [WebDriver] for uniform webpage parsing procedure.
 */
class JSoupDriver : WebDriver, SearchContext {
    private val cookies: MutableMap<String, String>
    private var options: Options? = null
    private var con: Connection? = null
    private var doc: Document? = null
    private var url: URL? = null

    /**
     * Returns the response status message.
     *
     * @return the status message
     */
    var statusMessage: String? = null
        private set

    override operator fun get(url: String) {
        get(url) {}
    }

    operator fun get(url: String, dataModifier: Connection?.() -> Unit) {
        generateConnection()
        dataModifier(con)
        try {
            val res = con!!.url(url).cookies(cookies).execute()
            statusMessage = res.statusMessage()
            cookies.putAll(res.cookies())
            doc = res.parse()
            this.url = URL(url)
        } catch (e: IOException) {
            w(javaClass.simpleName + " was not able to parse from URL: " + url, e)
            doc = null
            statusMessage = null
            this.url = null
        } catch (e: NullPointerException) {
            f(javaClass.simpleName + " is closed", e)
            doc = null
            statusMessage = null
            this.url = null
        }
    }

    override fun getCurrentUrl() = url?.toString()

    override fun getTitle() = doc?.title()

    override fun findElements(by: By): List<WebElement> {
        return if (doc == null) LinkedList() else by.findElements(JSoupWebElement(doc!!))
    }

    override fun findElement(by: By): WebElement? {
        return if (doc == null) null else by.findElement(JSoupWebElement(doc!!))
    }

    override fun getPageSource() = doc?.toString()

    override fun close() {
        url = null
        doc = null
        con = null
    }

    override fun quit() {
        close()
        options = null
    }

    override fun getWindowHandles(): Set<String>? {
        return null
    }

    override fun getWindowHandle(): String? {
        return null
    }

    override fun switchTo(): TargetLocator? {
        return null
    }

    override fun navigate(): Navigation? {
        return null
    }

    override fun manage(): Options? {
        return options
    }

    override fun toString(): String {
        return javaClass.simpleName + ": JSoup on " + System.getProperty("os.name") + " (" + options + ")"
    }

    private fun generateConnection() {
        con = HttpConnection().userAgent(USER_AGENT).ignoreHttpErrors(true)
    }

    private fun generateOptions() {
        options = object : Options {
            override fun addCookie(cookie: Cookie) {
                this@JSoupDriver.cookies[cookie.name] = cookie.value
            }

            override fun deleteCookieNamed(name: String) {
                this@JSoupDriver.cookies.remove(name)
            }

            override fun deleteCookie(cookie: Cookie) {
                this@JSoupDriver.cookies.remove(cookie.name, cookie.value)
            }

            override fun deleteAllCookies() {
                this@JSoupDriver.cookies.clear()
            }

            override fun getCookies(): Set<Cookie> {
                return this@JSoupDriver.cookies.entries.stream().map { (key, value) ->
                    Cookie(
                        key, value
                    )
                }.collect(Collectors.toSet())
            }

            override fun getCookieNamed(name: String): Cookie? {
                val value: String? = this@JSoupDriver.cookies[name]
                return if (value == null) null else Cookie(name, value)
            }

            override fun timeouts(): Timeouts? {
                return null
            }

            override fun ime(): ImeHandler? {
                return null
            }

            override fun window(): Window? {
                return null
            }

            override fun logs(): Logs? {
                return null
            }
        }
    }

    private class JSoupWebElement(private val element: Element) : WebElement, FindsByCssSelector, FindsByXPath,
        FindsById, FindsByName, FindsByClassName, FindsByTagName, FindsByLinkText {
        override fun click() {}
        override fun submit() {}
        override fun sendKeys(vararg keysToSend: CharSequence) {}
        override fun clear() {
            element.`val`("")
        }

        override fun getTagName(): String {
            return element.tagName()
        }

        override fun getAttribute(name: String): String? {
            return if (element.hasAttr(name)) element.attr(name) else null
        }

        override fun isSelected(): Boolean {
            return false
        }

        override fun isEnabled(): Boolean {
            return true
        }

        override fun getText(): String {
            return element.text()
        }

        override fun findElements(by: By): List<WebElement> {
            return by.findElements(this)
        }

        override fun findElement(by: By): WebElement {
            return by.findElement(this)
        }

        override fun isDisplayed(): Boolean {
            return false
        }

        override fun getLocation(): Point? {
            return null
        }

        override fun getSize(): Dimension? {
            return null
        }

        override fun getRect(): Rectangle? {
            return null
        }

        override fun getCssValue(propertyName: String): String {
            return ""
        }

        @Throws(WebDriverException::class)
        override fun <X> getScreenshotAs(target: OutputType<X>): X? {
            return null
        }

        override fun findElementByClassName(using: String): WebElement {
            return className.setSelector(using).findElement(this)
        }

        override fun findElementsByClassName(using: String): List<WebElement> {
            return className.setSelector(using).findElements(this)
        }

        override fun findElementById(using: String): WebElement {
            return id.setSelector(using).findElement(this)
        }

        override fun findElementsById(using: String): List<WebElement> {
            return id.setSelector(using).findElements(this)
        }

        override fun findElementByLinkText(using: String): WebElement {
            return linkText.setSelector(using).findElement(this)
        }

        override fun findElementsByLinkText(using: String): List<WebElement> {
            return linkText.setSelector(using).findElements(this)
        }

        override fun findElementByPartialLinkText(using: String): WebElement {
            return partialLinkText.setSelector(using).findElement(this)
        }

        override fun findElementsByPartialLinkText(using: String): List<WebElement> {
            return partialLinkText.setSelector(using).findElements(this)
        }

        override fun findElementByName(using: String): WebElement {
            return name.setSelector(using).findElement(this)
        }

        override fun findElementsByName(using: String): List<WebElement> {
            return name.setSelector(using).findElements(this)
        }

        override fun findElementByTagName(using: String): WebElement {
            return Companion.tagName.setSelector(using).findElement(this)
        }

        override fun findElementsByTagName(using: String): List<WebElement> {
            return Companion.tagName.setSelector(using).findElements(this)
        }

        override fun findElementByCssSelector(using: String): WebElement {
            return cssSelector.setSelector(using).findElement(this)
        }

        override fun findElementsByCssSelector(using: String): List<WebElement> {
            return cssSelector.setSelector(using).findElements(this)
        }

        override fun findElementByXPath(using: String): WebElement {
            return xPath.setSelector(using).findElement(this)
        }

        override fun findElementsByXPath(using: String): List<WebElement> {
            return xPath.setSelector(using).findElements(this)
        }

        private abstract class EditableBy : By() {
            protected var selector: String? = null
            fun setSelector(selector: String?): By {
                this.selector = selector
                return this
            }

            override fun findElement(context: SearchContext): WebElement? {
                return try {
                    super.findElement(context)
                } catch (ex: NoSuchElementException) {
                    null
                }
            }
        }

        companion object {
            // All Bys for JSoupWebElements
            private val cssSelector: EditableBy = object : EditableBy() {
                override fun findElements(context: SearchContext): List<WebElement> {
                    return (context as JSoupWebElement).element.allElements.stream()
                        .filter { element: Element -> element.cssSelector() == selector }
                        .map { element: Element -> JSoupWebElement(element) }
                        .collect(Collectors.toCollection { LinkedList() })
                }
            }
            private val xPath: EditableBy = object : EditableBy() {
                override fun findElements(context: SearchContext): List<WebElement>? {
                    return null
                }
            }
            private val id: EditableBy = object : EditableBy() {
                override fun findElements(context: SearchContext): List<WebElement> {
                    return (context as JSoupWebElement).element.getElementsByAttributeValueContaining("id", selector)
                        .stream().map { element: Element -> JSoupWebElement(element) }
                        .collect(Collectors.toCollection { LinkedList() })
                }
            }
            private val name: EditableBy = object : EditableBy() {
                override fun findElements(context: SearchContext): List<WebElement> {
                    return (context as JSoupWebElement).element.getElementsByAttributeValue("name", selector).stream()
                        .map { element: Element -> JSoupWebElement(element) }
                        .collect(Collectors.toCollection { LinkedList() })
                }
            }
            private val className: EditableBy = object : EditableBy() {
                override fun findElements(context: SearchContext): List<WebElement> {
                    return (context as JSoupWebElement).element.getElementsByClass(selector).stream()
                        .map { element: Element -> JSoupWebElement(element) }
                        .collect(Collectors.toCollection { LinkedList() })
                }
            }
            private val tagName: EditableBy = object : EditableBy() {
                override fun findElements(context: SearchContext): List<WebElement> {
                    return (context as JSoupWebElement).element.getElementsByTag(selector).stream()
                        .map { element: Element -> JSoupWebElement(element) }
                        .collect(Collectors.toCollection { LinkedList() })
                }
            }
            private val linkText: EditableBy = object : EditableBy() {
                override fun findElements(context: SearchContext): List<WebElement> {
                    return (context as JSoupWebElement).element.getElementsMatchingText(selector).stream()
                        .filter { element: Element -> element.tagName() == "a" }
                        .map { element: Element -> JSoupWebElement(element) }
                        .collect(Collectors.toCollection { LinkedList() })
                }
            }
            private val partialLinkText: EditableBy = object : EditableBy() {
                override fun findElements(context: SearchContext): List<WebElement> {
                    return (context as JSoupWebElement).element.getElementsContainingText(selector).stream()
                        .filter { element: Element -> element.tagName() == "a" }
                        .map { element: Element -> JSoupWebElement(element) }
                        .collect(Collectors.toCollection { LinkedList() })
                }
            }
        }
    }

    companion object {
        private const val USER_AGENT = "Mozilla/5.0 (Linux x86_64; rv:84.0) Gecko/20100101 Firefox/84.0"
    }

    init {
        cookies = HashMap()
        generateOptions()
    }
}