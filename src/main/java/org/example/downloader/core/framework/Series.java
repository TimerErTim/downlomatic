package org.example.downloader.core.framework;

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
    protected final URL seriesURL;
    private final Set<Downloader> downloaders;

    protected Series(URL seriesURL) {
        this.seriesURL = seriesURL;
        this.downloaders = new LinkedHashSet<>();
    }

    protected Series(String seriesURLString) throws MalformedURLException {
        this(new URL(seriesURLString));
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
     * Each call of this method freshly generates Downloader objects.
     *
     * @throws MalformedURLException error when referring to a page without a valid series.
     */
    public void generateDownloaders() throws MalformedURLException {
        downloaders.clear();
        Set<? extends Downloader> collection = generateEpisodeDownloaders();
        if (collection.isEmpty()) {
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

    protected abstract Set<? extends Downloader> generateEpisodeDownloaders();

    /**
     * Gets the error message when the Series can't find a
     * list of downloadable videos.
     *
     * @return the error message
     */
    protected abstract String getInvalidSeriesMessage();

    @Override
    @Nonnull
    public Iterator<Downloader> iterator() {
        return downloaders.iterator();
    }
}
