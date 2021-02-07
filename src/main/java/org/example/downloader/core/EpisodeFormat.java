package org.example.downloader.core;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EpisodeFormat {
    private final List<Identifier> identifiers;

    private EpisodeFormat(String seriesName, String seasonNumber, String episodeNumber, String episodeName) {
        this.identifiers = new LinkedList<>();
        identifiers.add(new Identifier("S", seriesName));
        identifiers.add(new Identifier("s", seasonNumber));
        identifiers.add(new Identifier("E", episodeName));
        identifiers.add(new Identifier("e", episodeNumber));
    }

    /**
     * Formats the episodes description to a readable String using the given expression.
     * <b> Important to read this documentation </b>
     * <p>
     * There are four types of identifiers (without quotation marks):<ul>
     * <li>"<b>/S</b>": Means the name of the series
     * <li>"<b>/s</b>": Means the number of the season
     * <li>"<b>/E</b>": Means the name of the episode
     * <li>"<b>/e</b>": Means the number of the episode
     * <li>"<b>/[</b>" or "<b>/]</b>": Covered in the last paragraph
     * </ul><p>
     * If those identifiers are found in the given expression, they are
     * replaced by their respective meaning.<br>
     * Every other part of the expression (exception explained in the following
     * paragraph) is seen as literal and returned as
     * is.
     * <p>
     * Illegal characters in the expression will be removed. Illegal characters are characters,
     * which can not be used in the name of a file. This includes Windows specific illegals.
     * <p>
     * If there is an identifier in the expression, which can't be filled because
     * the according field in this Object is null, the identifier by default is left out
     * and simply removed. You can adjust this behavior by using the last (two) identifiers
     * of the above list, which are <b>/[</b> and <b>/]</b>. You can put these identifiers
     * around a segment of the expression. The segment will only be displayed in the formatted
     * text if the other identifiers inside that segment can successfully be filled in. If there
     * is no other identifier inside or a missing closing/opening identifier, the segment will be
     * returned as is, which basically means, that the identifiers causing the problem are ignored.
     * <p>
     * In each of these segments you can make use of "inverted identifiers". Inverted identifiers make
     * the segment they are in only successful (and thus visible) if there exists no value for them.
     * They can be created by putting a "!" directly after "/" of each identifier. For example
     * {@code "/[S/sE/e/]/[/!sEpisode /E]} would create "Episode 1" for the first Episode which doesn't have a
     * season number. If it has one, it creates "S1E1" (assuming it's the first season).
     *
     * @param expression a String expression used as template for formatting
     * @return a formatted String representation of an episode
     */
    //TODO: Add more identifiers
    public String format(String expression) {
        final String regex = "\\/\\[(?<content>[^\\/]+[^\\[\\]]*?)\\/\\]";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);

        Matcher matcher = pattern.matcher(expression);
        while (matcher.find()) {
            expression = matcher.replaceAll((match) -> formatSegment(match.group(1)));
            matcher = pattern.matcher(expression);
        }


        String temp = expression.replaceAll("(\\/\\[|\\/\\])", "");
        for (Identifier identifier : identifiers) {
            temp = temp.replaceAll(identifier.getReplaceRegex(), identifier.getValue());
        }
        return temp.replaceAll("[\\\\\\/:*?\"<>|]", "");
    }

    private String formatSegment(String segment) {
        for (Identifier identifier : identifiers) {
            boolean hasValue = identifier.hasValue();
            if (segment.contains(identifier.getIdentifier()) && !hasValue || segment.contains(identifier.getNegativeIdentifier()) && hasValue) {
                return "";
            } else {
                segment = segment.replaceAll("(" +
                                Pattern.quote(identifier.getIdentifier()) + "|" +
                                Pattern.quote(identifier.getNegativeIdentifier()) + ")",
                        identifier.getValue());
            }
        }

        return segment;
    }

    /**
     * Generates a non mutable EpisodeFormat given specific parameters.
     */
    public static class EpisodeFormatGenerator {
        private String episodeName, seriesName;
        private String episodeNumber;
        private String seasonNumber;

        /**
         * Generates a EpisodeFormatGenerator. It's only
         * purpose is to generate a non mutable EpisodeFormat.
         *
         * @param seriesName    the series' name
         * @param seasonNumber  the number of the season
         * @param episodeNumber the number of the episode
         * @param episodeName   the name of the episode
         */
        public EpisodeFormatGenerator(String seriesName, String seasonNumber, String episodeNumber, String episodeName) {
            this.episodeName = episodeName;
            this.seriesName = seriesName;
            this.episodeNumber = episodeNumber;
            this.seasonNumber = seasonNumber;
        }

        /**
         * Generates a EpisodeFormatGenerator. It's only
         * purpose is to generate a non mutable EpisodeFormat.
         */
        public EpisodeFormatGenerator() {
            this(null, null, null, null);
        }

        /**
         * Sets the series name of the EpisodeFormat.
         *
         * @param seriesName the series' name
         * @return this Object for easy chaining of expressions.
         */
        public EpisodeFormatGenerator setSeriesName(String seriesName) {
            this.seriesName = seriesName;
            return this;
        }

        /**
         * Sets the season number of the EpisodeFormat.
         *
         * @param seasonNumber the number of the season
         * @return this Object for easy chaining of expressions.
         */
        public EpisodeFormatGenerator setSeasonNumber(String seasonNumber) {
            this.seasonNumber = seasonNumber;
            return this;
        }

        /**
         * Sets the episode number of the EpisodeFormat.
         *
         * @param episodeNumber the number of the episode
         * @return this Object for easy chaining of expressions.
         */
        public EpisodeFormatGenerator setEpisodeNumber(String episodeNumber) {
            this.episodeNumber = episodeNumber;
            return this;
        }

        /**
         * Sets the episode name of the EpisodeFormat.
         *
         * @param episodeName the name of the episode
         * @return this Object for easy chaining of expressions.
         */
        public EpisodeFormatGenerator setEpisodeName(String episodeName) {
            this.episodeName = episodeName;
            return this;
        }

        /**
         * Generates a non mutable EpisodeFormat with
         * the configured entries.
         *
         * @return a final EpisodeFormat
         */
        public EpisodeFormat generate() {
            return new EpisodeFormat(seriesName, seasonNumber, episodeNumber, episodeName);
        }
    }

    private static class Identifier {
        private final String identifier, negativeIdentifier, value;

        private Identifier(String key, String value) {
            this.identifier = "/" + key;
            this.negativeIdentifier = "/!" + key;
            this.value = value;
        }

        private boolean hasValue() {
            return value != null;
        }

        private String getIdentifier() {
            return identifier;
        }

        private String getNegativeIdentifier() {
            return negativeIdentifier;
        }

        private String getReplaceRegex() {
            return "(" + Pattern.quote(getIdentifier()) + "|" +
                    Pattern.quote(getNegativeIdentifier()) + ")";
        }

        private String getValue() {
            return (value != null ? value : "");
        }
    }
}
