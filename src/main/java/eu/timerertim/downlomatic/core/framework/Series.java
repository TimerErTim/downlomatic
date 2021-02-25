package eu.timerertim.downlomatic.core.framework;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A collection of individual {@link Downloader}s. Provides some neat
 * and useful methods.
 */
public abstract class Series implements Iterable<Downloader> {
    protected final Page page;
    private final URL seriesURL;
    private final Set<Downloader> downloaders;
    private String name;

    protected Series(URL seriesURL) throws MalformedURLException {
        this.seriesURL = seriesURL;
        this.downloaders = new LinkedHashSet<>();
        this.name = "";
        this.page = getPage();
        if (!isValidSeriesURL(seriesURL)) {
            throw new MalformedURLException("URL " + seriesURL + " is no URL leading to a " + page.getPageDomain() + " episode list");
        }
    }

    protected Series(String seriesURLString) throws MalformedURLException {
        this(new URL(seriesURLString));
    }

    protected Series(String seriesURLString, String name) throws MalformedURLException {
        this(seriesURLString);
        this.name = name;
    }

    /**
     * Returns a {@code Series} object containing the given {@code Downloader}s
     * and name.
     * <p>
     * Not that the resulting instance will neither have a source URL
     * nor a {@link Page} to reference to.
     * <p>
     * If any complications occur, which really shouldn't happen, null is
     * returned instead.
     *
     * @param downloaders the {@code Set} of {@code Downloader}s
     * @return an anonymous {@code Series} instance or null if that is not possible
     */
    public static Series custom(Set<? extends Downloader> downloaders) {
        try {
            return new Series((URL) null) {
                @Override
                protected Set<? extends Downloader> parseSeries(URL seriesURL) {
                    return downloaders;
                }

                @Override
                protected boolean isValidSeriesURL(URL seriesURL) {
                    return true;
                }

                @Override
                public Page getPage() {
                    return new Page() {
                        @Override
                        public long getPageDelay() {
                            return 0;
                        }

                        @Override
                        public boolean isValidPageURL(URL url) {
                            return true;
                        }

                        @Override
                        public String getPageDomain() {
                            return "";
                        }
                    };
                }
            };
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * Generates a List of Downloaders, each one leading to one episode
     * of this Series, if that List has never been generated before.
     * The List is accessible through {@link Series#iterator()}.
     * <p>
     * Subsequent calls of this method are ignored, as the {@link Downloader}s
     * have already been generated. To generate them once again, you can
     * invoke {@link Series#generateDownloaders()}.
     *
     * @throws MalformedURLException error when referring to a page without a valid series.
     */
    public void fillDownloaders() throws MalformedURLException {
        if (isEmpty()) {
            generateDownloaders();
        }
    }

    /**
     * Generates a List of Downloaders, each one leading to one episode
     * of this Series. The List is accessible through {@link Series#iterator()}.
     * <p>
     * Each call of this method freshly generates Downloader objects. It also parses
     * this Series name from the WebSite.
     *
     * @throws MalformedURLException error when referring to a page without a valid series.
     */
    public void generateDownloaders() throws MalformedURLException {
        downloaders.clear();
        Set<? extends Downloader> collection = parseSeries(seriesURL);
        if (collection == null) {
            throw new MalformedURLException(getInvalidSeriesMessage());
        } else {
            downloaders.addAll(collection);
        }
    }

    /**
     * Returns true if the {@code Series} object
     * is empty.
     * <p>
     * Because it is only empty if the generation of
     * {@link Downloader}s failed or they were never
     * generated in the first place, this method
     * can also be used to test the behavior of
     * {@link Series#fillDownloaders()}.
     *
     * @return true if this object is empty
     */
    public boolean isEmpty() {
        return downloaders.isEmpty();
    }

    /**
     * Returns the name of this {@code Series} object.
     * <p>
     * The name is only indented for use in graphics application and
     * doesn't change the framework behavior. May be used to your likings
     * however.
     *
     * @return the name
     */
    public String getName() {
        return name != null ? name : "";
    }

    /**
     * Gets the error message when the Series can't find a
     * list of downloadable videos.
     *
     * @return the error message
     */
    protected String getInvalidSeriesMessage() {
        return "URL \"" + seriesURL + "\" is no valid series on " + page.getPageDomain();
    }

    /**
     * Returns the {@code Page} that is associated with this {@code Series}.
     *
     * @return the associated {@code Page}.
     */
    public abstract Page getPage();

    protected abstract boolean isValidSeriesURL(URL seriesURL);

    protected abstract Set<? extends Downloader> parseSeries(URL seriesURL);

    @Override
    @Nonnull
    public Iterator<Downloader> iterator() {
        return downloaders.iterator();
    }
}
