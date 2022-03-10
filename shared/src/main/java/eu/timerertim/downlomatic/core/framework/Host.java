package eu.timerertim.downlomatic.core.framework;

import eu.timerertim.downlomatic.utils.logging.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A {@code Host} defines the interaction with a website
 */
public abstract class Host implements Page {
    private final URL listURL;
    private Set<Series> series;

    /**
     * Creates a standard {@code Host}.
     * <p>
     * If {@link Host#getListURL()} throws {@link MalformedURLException}, {@link Host#parsePage(URL)} will
     * be given null.
     */
    protected Host() {
        URL listURL;
        try {
            listURL = getListURL();
        } catch (MalformedURLException e) {
            Log.wtf(getPageDomain() + " Host has no valid URL to parse Series from.", e);
            listURL = null;
        }
        this.listURL = listURL;
    }

    /**
     * Generates a {@code Set} of {@code Series}, each one leading to a collection
     * of {@code Downloader}s, containing every {@code Series} retrievable from
     * this {@code Host} object.
     * <p>
     * Each call of this method freshly fetches this {@code Host}'s {@link Series}.
     *
     * @return the generated {@code Set}
     * @throws MalformedURLException is thrown if the {@code Page} can not be reached
     *                               (not no {@code Series} found)
     */
    public Set<Series> generateSeries() throws MalformedURLException {
        Set<Series> series;
        if (listURL == null || (series = parsePage(listURL)) == null) {
            throw new MalformedURLException(getInvalidHostMessage());
        }
        return (this.series = series);
    }

    /**
     * Gets a {@code Set} of {@code Series}, each one leading to a collection
     * of {@code Downloader}s, containing every {@code Series} retrievable from
     * this {@code Host} object.
     * <p>
     * Automatically calls {@link Host#generateSeries()} if no {@code Set} has
     * been generated as of yet.
     *
     * @return the newest {@code Set}
     * @throws MalformedURLException is thrown if the {@code Page} can not be reached
     *                               (not no {@code Series} found)
     */
    public Set<Series> getSeries() throws MalformedURLException {
        if (series == null) {
            generateSeries();
        }
        return series;
    }

    /**
     * Searches the content of {@link Host#getSeries()} and returns
     * every {@code Series} object which name contains the given {@code String}.
     *
     * @param search the {@code String} the returned {@code Series} have to contain
     * @return a {@code Set} of filtered {@code Series}
     * @throws MalformedURLException is thrown if the {@code Page} can not be reached
     *                               (not no {@code Series} found)
     */
    public Set<Series> findSeries(String search) throws MalformedURLException {
        return getSeries().stream().
                filter(searchAgainst -> searchAgainst.getName().contains(search)).
                collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Gets the error message when the {@code Host} is
     * not able to get a list of all {@code Series} available
     * due to errors.
     *
     * @return the error message
     */
    protected String getInvalidHostMessage() {
        return "Host \"" + getPageDomain() + "\" can not parse series";
    }

    /**
     * Returns a {@code Series} instance of the type that is associated
     * with this {@code Host}.
     *
     * @param seriesURL the URL of which a {@code Series} object should be created
     * @return a {@code Series} that is associated with this {@code Host}
     * @throws MalformedURLException the exception that is thrown if the URL is no valid series
     */
    public abstract Series getSeries(URL seriesURL) throws MalformedURLException;

    /**
     * Returns a {@code Downloader} instance of the type that is associated
     * with this {@code Host}.
     *
     * @param videoURL the URL of which a {@code Downloader} object should be created
     * @return a {@code Downloader} that is associated with this {@code Host}
     * @throws MalformedURLException the exception that is thrown if the URL is no valid video
     */
    public abstract Downloader getDownloader(URL videoURL) throws MalformedURLException;

    /**
     * Returns the URL under which this {@code Host} is able to parse all available {@code Series}.
     *
     * @return the URL with all available {@code Series}
     * @throws MalformedURLException should never happen and indicates a typo in the implementation
     */
    protected abstract URL getListURL() throws MalformedURLException;

    /**
     * Parses a Set of {@code Series} from the given URL.
     * <p>
     * The resulting {@link Series} can be used to search for {@link Downloader}s and download every video from the
     * represented website.
     * <p>
     * If any problems occur, null is returned.
     *
     * @param listURL typically {@link Host#getListURL()}
     * @return a Set of {@code Series} or null on error
     */
    protected abstract Set<Series> parsePage(URL listURL);
}
