package org.example.downloader.core.format;

/**
 * Generates a non mutable EpisodeFormat given specific parameters.
 */
public class EpisodeFormatBuilder {
    private String episodeName, seriesName;
    private String episodeNumber, seasonNumber;
    private String language, translationType;

    /**
     * Generates a EpisodeFormatBuilder. It's only
     * purpose is to build a non mutable EpisodeFormat.
     *
     * @param seriesName    the series' name
     * @param seasonNumber  the number of the season
     * @param episodeNumber the number of the episode
     * @param episodeName   the name of the episode
     */
    public EpisodeFormatBuilder(String seriesName, String seasonNumber, String episodeNumber, String episodeName,
                                String language, String translationType) {
        this.episodeName = episodeName;
        this.seriesName = seriesName;
        this.episodeNumber = episodeNumber;
        this.seasonNumber = seasonNumber;
        this.language = language;
        this.translationType = translationType;
    }

    /**
     * Generates a EpisodeFormatBuilder. It's only
     * purpose is to build a non mutable EpisodeFormat.
     */
    public EpisodeFormatBuilder() {
        this(null, null, null, null, null, null);
    }

    /**
     * Sets the series name of the EpisodeFormat.
     *
     * @param seriesName the series' name
     * @return this Object for easy chaining of expressions.
     */
    public EpisodeFormatBuilder setSeriesName(String seriesName) {
        this.seriesName = seriesName;
        return this;
    }

    /**
     * Sets the season number of the EpisodeFormat.
     *
     * @param seasonNumber the number of the season
     * @return this Object for easy chaining of expressions.
     */
    public EpisodeFormatBuilder setSeasonNumber(String seasonNumber) {
        this.seasonNumber = seasonNumber;
        return this;
    }

    /**
     * Sets the episode number of the EpisodeFormat.
     *
     * @param episodeNumber the number of the episode
     * @return this Object for easy chaining of expressions.
     */
    public EpisodeFormatBuilder setEpisodeNumber(String episodeNumber) {
        this.episodeNumber = episodeNumber;
        return this;
    }

    /**
     * Sets the episode name of the EpisodeFormat.
     *
     * @param episodeName the name of the episode
     * @return this Object for easy chaining of expressions.
     */
    public EpisodeFormatBuilder setEpisodeName(String episodeName) {
        this.episodeName = episodeName;
        return this;
    }

    /**
     * Sets the translation type of the EpisodeFormat.
     *
     * @param translationType the method of displaying the audience language (sub, dub, raw/none/OV, etc.)
     * @return this Object for easy chaining of expressions.
     */
    public EpisodeFormatBuilder setTranslationType(String translationType) {
        this.translationType = translationType;
        return this;
    }

    /**
     * Sets the language of the EpisodeFormat.
     *
     * @param language the audience language of the episode
     * @return this Object for easy chaining of expressions.
     */
    public EpisodeFormatBuilder setLanguage(String language) {
        this.language = language;
        return this;
    }

    /**
     * Generates a non mutable EpisodeFormat with
     * the configured entries.
     *
     * @return a final EpisodeFormat
     */
    public EpisodeFormat build() {
        return new EpisodeFormat(seriesName, seasonNumber, episodeNumber, episodeName, language, translationType);
    }
}
