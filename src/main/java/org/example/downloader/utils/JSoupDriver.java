package org.example.downloader.utils;

import org.jsoup.Connection;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.*;
import org.openqa.selenium.internal.*;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class JSoupDriver implements WebDriver, SearchContext {
    private final static String USER_AGENT = "Mozilla/5.0 (Linux x86_64; rv:84.0) Gecko/20100101 Firefox/84.0";

    private Connection con;
    private Document doc;
    private URL url;

    public JSoupDriver() {
        con = new HttpConnection().userAgent(USER_AGENT);
    }

    @Override
    public void get(String url) {
        try {
            doc = con.url(url).get();
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
    }

    @Override
    public void quit() {
        close();
        con = null;
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
        //TODO: implement
        return null;
    }

    private static class JSoupWebElement implements WebElement, FindsByCssSelector, FindsByXPath, FindsById, FindsByName, FindsByClassName, FindsByTagName, FindsByLinkText {
        // All Bys for JSoupWebElements
        private static final By cssSelector = new EditableBy() {
            public List<WebElement> findElements(SearchContext context) {
                return ((JSoupWebElement) context).element.getAllElements().stream().
                        filter(element -> element.cssSelector().equals(selector)).
                        map(JSoupWebElement::new).
                        collect(Collectors.toCollection(LinkedList::new));
            }
        };
        private static final By xPath = new EditableBy() {
            public List<WebElement> findElements(SearchContext context) {
                return null;
            }
        };
        private static final By id = new EditableBy() {
            public List<WebElement> findElements(SearchContext context) {
                return ((JSoupWebElement) context).element.getAllElements().stream().
                        filter(element -> element.id().equals(selector)).
                        map(JSoupWebElement::new).
                        collect(Collectors.toCollection(LinkedList::new));
            }
        };
        private static final By name = new EditableBy() {
            public List<WebElement> findElements(SearchContext context) {
                return ((JSoupWebElement) context).element.getAllElements().stream().
                        filter(element -> element.nodeName().equals(selector)).
                        map(JSoupWebElement::new).
                        collect(Collectors.toCollection(LinkedList::new));
            }
        };
        private static final By className = new EditableBy() {
            public List<WebElement> findElements(SearchContext context) {
                return ((JSoupWebElement) context).element.
                        getElementsByClass(selector).stream().
                        map(JSoupWebElement::new).
                        collect(Collectors.toCollection(LinkedList::new));
            }
        };
        private static final By tagName = new EditableBy() {
            public List<WebElement> findElements(SearchContext context) {
                return ((JSoupWebElement) context).element.
                        getElementsByTag(selector).stream().
                        map(JSoupWebElement::new).
                        collect(Collectors.toCollection(LinkedList::new));
            }
        };
        private static final By linkText = new EditableBy() {
            public List<WebElement> findElements(SearchContext context) {
                //TODO: Implement
                return ((JSoupWebElement) context).element.
                        getElementsByTag(selector).stream().
                        map(JSoupWebElement::new).
                        collect(Collectors.toCollection(LinkedList::new));
            }
        };
        private static final By partialLinkText = new EditableBy() {
            public List<WebElement> findElements(SearchContext context) {
                //TODO: Implement
                return ((JSoupWebElement) context).element.
                        getElementsByTag(selector).stream().
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
            return false;
        }

        @Override
        public String getText() {
            return null;
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
            return null;
        }

        @Override
        public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
            return null;
        }

        @Override
        public WebElement findElementByClassName(String using) {
            return null;
        }

        @Override
        public List<WebElement> findElementsByClassName(String using) {
            return null;
        }

        @Override
        public WebElement findElementById(String using) {
            return null;
        }

        @Override
        public List<WebElement> findElementsById(String using) {
            return null;
        }

        @Override
        public WebElement findElementByLinkText(String using) {
            return null;
        }

        @Override
        public List<WebElement> findElementsByLinkText(String using) {
            return null;
        }

        @Override
        public WebElement findElementByPartialLinkText(String using) {
            return null;
        }

        @Override
        public List<WebElement> findElementsByPartialLinkText(String using) {
            return null;
        }

        @Override
        public WebElement findElementByName(String using) {
            return null;
        }

        @Override
        public List<WebElement> findElementsByName(String using) {
            return null;
        }

        @Override
        public WebElement findElementByTagName(String using) {
            return null;
        }

        @Override
        public List<WebElement> findElementsByTagName(String using) {
            return null;
        }

        @Override
        public WebElement findElementByCssSelector(String using) {
            return null;
        }

        @Override
        public List<WebElement> findElementsByCssSelector(String using) {
            return null;
        }

        @Override
        public WebElement findElementByXPath(String using) {
            return null;
        }

        @Override
        public List<WebElement> findElementsByXPath(String using) {
            return null;
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
