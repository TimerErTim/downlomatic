package org.example.downloader.core;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public abstract class Series implements Iterable<Downloader> {
    protected final URL seriesURL;
    private final List<Downloader> sources;

    protected Series(URL seriesURL) throws MalformedURLException {
        this.seriesURL = seriesURL;
        sources = new LinkedList<>();
    }

    protected Series(String seriesURLString) throws MalformedURLException {
        this(new URL(seriesURLString));
    }

    /**
     * Generates a List of Downloaders, each one leading to one episode
     * of this Series. The List is accessible through {@link Series#iterator()}.
     *
     * @throws MalformedURLException error when referring to a page without a valid series.
     */
    public void fillEpisodeDownloaders() throws MalformedURLException {
        sources.clear();
        Collection<? extends Downloader> collection = generateEpisodeDownloaders();
        if (collection.isEmpty()) {
            throw new MalformedURLException(getInvalidSeriesMessage());
        } else {
            sources.addAll(collection);
        }
    }

    protected abstract Set<? extends Downloader> generateEpisodeDownloaders();

    /**
     * Returns whether or not this Series
     * requires JavaScript in order to generate a
     * list of Downloaders.
     * <p>
     * Useful for guessing the computation time when generating
     * a list of {@link Downloader}s.
     *
     * @return if JavaScript is required for execution of {@link Series#fillEpisodeDownloaders()}
     */
    public abstract boolean needsJavaScript();

    /**
     * Gets the error message when the Series can't find a
     * list of downloadable videos.
     *
     * @return the error message
     */
    public abstract String getInvalidSeriesMessage();

    @Override
    @Nonnull
    public Iterator<Downloader> iterator() {
        return sources.iterator();
    }
}
