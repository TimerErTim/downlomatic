package eu.timerertim.downlomatic.utils;

import org.jsoup.Connection;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.internal.*;
import org.openqa.selenium.logging.Logs;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * JSoup based implementation of Selenium's {@link WebDriver} for uniform webpage parsing procedure.
 */
public class JSoupDriver implements WebDriver, SearchContext {
    private final static String USER_AGENT = "Mozilla/5.0 (Linux x86_64; rv:84.0) Gecko/20100101 Firefox/84.0";

    private final Map<String, String> cookies;
    private Options options;
    private Connection con;
    private Document doc;
    private URL url;

    public JSoupDriver() {
        cookies = new HashMap<>();
        generateOptions();
    }

    @Override
    public void get(String url) {
        get(url, (con) -> {
        });
    }

    public void get(String url, Consumer<Connection> dataModifier) {
        generateConnection();
        dataModifier.accept(con);
        try {
            Connection.Response res = con.url(url).cookies(cookies).execute();
            cookies.putAll(res.cookies());
            doc = res.parse();
            this.url = new URL(url);
        } catch (IOException e) {
            java.util.logging.Logger.getLogger("org.openqa.selenium").log(Level.WARNING, getClass().getSimpleName() + " was not able to parse from URL: " + url, e);
            doc = null;
            this.url = null;
        } catch (NullPointerException e) {
            java.util.logging.Logger.getLogger("org.openqa.selenium").log(Level.SEVERE, getClass().getSimpleName() + " is closed", e);
            doc = null;
            this.url = null;
        }
    }

    @Override
    public String getCurrentUrl() {
        if (url != null) {
            return url.toString();
        } else
            return null;
    }

    @Override
    public String getTitle() {
        if (doc != null) {
            return doc.title();
        } else
            return null;
    }

    @Override
    public List<WebElement> findElements(By by) {
        return by.findElements(new JSoupWebElement(doc));
    }

    @Override
    public WebElement findElement(By by) {
        return by.findElement(new JSoupWebElement(doc));
    }

    @Override
    public String getPageSource() {
        if (doc != null) {
            return doc.toString();
        } else
            return null;
    }

    @Override
    public void close() {
        url = null;
        doc = null;
        con = null;
    }

    @Override
    public void quit() {
        close();
        options = null;
    }

    @Override
    public Set<String> getWindowHandles() {
        return null;
    }

    @Override
    public String getWindowHandle() {
        return null;
    }

    @Override
    public TargetLocator switchTo() {
        return null;
    }

    @Override
    public Navigation navigate() {
        return null;
    }

    @Override
    public Options manage() {
        return options;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": JSoup on " + System.getProperty("os.name") + " (" + options + ")";
    }

    private void generateConnection() {
        con = new HttpConnection().userAgent(USER_AGENT);
    }

    private void generateOptions() {
        options = new Options() {
            @Override
            public void addCookie(Cookie cookie) {
                cookies.put(cookie.getName(), cookie.getValue());
            }

            @Override
            public void deleteCookieNamed(String name) {
                cookies.remove(name);
            }

            @Override
            public void deleteCookie(Cookie cookie) {
                cookies.remove(cookie.getName(), cookie.getValue());
            }

            @Override
            public void deleteAllCookies() {
                cookies.clear();
            }

            @Override
            public Set<Cookie> getCookies() {
                return cookies.entrySet().stream().map((cookieEntry) -> new Cookie(cookieEntry.getKey(), cookieEntry.getValue())).collect(Collectors.toSet());
            }

            @Override
            public Cookie getCookieNamed(String name) {
                String value = cookies.get(name);
                return (value == null ? null : new Cookie(name, value));
            }

            @Override
            public Timeouts timeouts() {
                return null;
            }

            @Override
            public ImeHandler ime() {
                return null;
            }

            @Override
            public Window window() {
                return null;
            }

            @Override
            public Logs logs() {
                return null;
            }
        };
    }

    private static class JSoupWebElement implements WebElement, FindsByCssSelector, FindsByXPath, FindsById, FindsByName, FindsByClassName, FindsByTagName, FindsByLinkText {
        // All Bys for JSoupWebElements
        private static final EditableBy cssSelector = new EditableBy() {
            public List<WebElement> findElements(SearchContext context) {
                return ((JSoupWebElement) context).element.getAllElements().stream().
                        filter(element -> element.cssSelector().equals(selector)).
                        map(JSoupWebElement::new).
                        collect(Collectors.toCollection(LinkedList::new));
            }
        };
        private static final EditableBy xPath = new EditableBy() {
            public List<WebElement> findElements(SearchContext context) {
                return null;
            }
        };
        private static final EditableBy id = new EditableBy() {
            public List<WebElement> findElements(SearchContext context) {
                return ((JSoupWebElement) context).element.
                        getElementsByAttributeValueContaining("id", selector).stream().
                        map(JSoupWebElement::new).
                        collect(Collectors.toCollection(LinkedList::new));
            }
        };
        private static final EditableBy name = new EditableBy() {
            public List<WebElement> findElements(SearchContext context) {
                return ((JSoupWebElement) context).element.
                        getElementsByAttributeValue("name", selector).stream().
                        map(JSoupWebElement::new).
                        collect(Collectors.toCollection(LinkedList::new));
            }
        };
        private static final EditableBy className = new EditableBy() {
            public List<WebElement> findElements(SearchContext context) {
                return ((JSoupWebElement) context).element.
                        getElementsByClass(selector).stream().
                        map(JSoupWebElement::new).
                        collect(Collectors.toCollection(LinkedList::new));
            }
        };
        private static final EditableBy tagName = new EditableBy() {
            public List<WebElement> findElements(SearchContext context) {
                return ((JSoupWebElement) context).element.
                        getElementsByTag(selector).stream().
                        map(JSoupWebElement::new).
                        collect(Collectors.toCollection(LinkedList::new));
            }
        };
        private static final EditableBy linkText = new EditableBy() {
            public List<WebElement> findElements(SearchContext context) {
                return ((JSoupWebElement) context).element.
                        getElementsMatchingText(selector).stream().filter(element -> element.tagName().equals("a")).
                        map(JSoupWebElement::new).
                        collect(Collectors.toCollection(LinkedList::new));
            }
        };
        private static final EditableBy partialLinkText = new EditableBy() {
            public List<WebElement> findElements(SearchContext context) {
                return ((JSoupWebElement) context).element.
                        getElementsContainingText(selector).stream().filter(element -> element.tagName().equals("a")).
                        map(JSoupWebElement::new).
                        collect(Collectors.toCollection(LinkedList::new));
            }
        };
        private final Element element;

        public JSoupWebElement(Element element) {
            this.element = element;
        }

        @Override
        public void click() {
        }

        @Override
        public void submit() {
        }

        @Override
        public void sendKeys(CharSequence... keysToSend) {
        }

        @Override
        public void clear() {
            element.val("");
        }

        @Override
        public String getTagName() {
            return element.tagName();
        }

        @Override
        public String getAttribute(String name) {
            return element.attr(name);
        }

        @Override
        public boolean isSelected() {
            return false;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public String getText() {
            return element.text();
        }

        @Override
        public List<WebElement> findElements(By by) {
            return by.findElements(this);
        }

        @Override
        public WebElement findElement(By by) {
            return by.findElement(this);
        }

        @Override
        public boolean isDisplayed() {
            return false;
        }

        @Override
        public Point getLocation() {
            return null;
        }

        @Override
        public Dimension getSize() {
            return null;
        }

        @Override
        public Rectangle getRect() {
            return null;
        }

        @Override
        public String getCssValue(String propertyName) {
            return "";
        }

        @Override
        public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
            return null;
        }

        @Override
        public WebElement findElementByClassName(String using) {
            return className.setSelector(using).findElement(this);
        }

        @Override
        public List<WebElement> findElementsByClassName(String using) {
            return className.setSelector(using).findElements(this);
        }

        @Override
        public WebElement findElementById(String using) {
            return id.setSelector(using).findElement(this);
        }

        @Override
        public List<WebElement> findElementsById(String using) {
            return id.setSelector(using).findElements(this);
        }

        @Override
        public WebElement findElementByLinkText(String using) {
            return linkText.setSelector(using).findElement(this);
        }

        @Override
        public List<WebElement> findElementsByLinkText(String using) {
            return linkText.setSelector(using).findElements(this);
        }

        @Override
        public WebElement findElementByPartialLinkText(String using) {
            return partialLinkText.setSelector(using).findElement(this);
        }

        @Override
        public List<WebElement> findElementsByPartialLinkText(String using) {
            return partialLinkText.setSelector(using).findElements(this);
        }

        @Override
        public WebElement findElementByName(String using) {
            return name.setSelector(using).findElement(this);
        }

        @Override
        public List<WebElement> findElementsByName(String using) {
            return name.setSelector(using).findElements(this);
        }

        @Override
        public WebElement findElementByTagName(String using) {
            return tagName.setSelector(using).findElement(this);
        }

        @Override
        public List<WebElement> findElementsByTagName(String using) {
            return tagName.setSelector(using).findElements(this);
        }

        @Override
        public WebElement findElementByCssSelector(String using) {
            return cssSelector.setSelector(using).findElement(this);
        }

        @Override
        public List<WebElement> findElementsByCssSelector(String using) {
            return cssSelector.setSelector(using).findElements(this);
        }

        @Override
        public WebElement findElementByXPath(String using) {
            return xPath.setSelector(using).findElement(this);
        }

        @Override
        public List<WebElement> findElementsByXPath(String using) {
            return xPath.setSelector(using).findElements(this);
        }

        private Element getElement() {
            return element;
        }

        private abstract static class EditableBy extends By {
            protected String selector;

            public By setSelector(String selector) {
                this.selector = selector;
                return this;
            }

            @Override
            public WebElement findElement(SearchContext context) {
                try {
                    return super.findElement(context);
                } catch (NoSuchElementException ex) {
                    return null;
                }
            }
        }
    }
}
