package eu.timerertim.downlomatic.core.format;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Allows to format video titles comparable to {@link java.time.format.DateTimeFormatter}.
 * <p>
 * Due to this being immutable it has to be built with a {@link EpisodeFormatBuilder}. The formatting happens
 * through {@link EpisodeFormat#format(String)} which takes a "template" as input. Everything you need to know is
 * described in that methods JavaDoc documentation.
 */
public class EpisodeFormat {
    public final static String DESCRIPTION = "These are the types of identifiers:\n" +
            "\"/S\": Means the name of the series\n" +
            "\"/s\": Means the number of the season\n" +
            "\"/E\": Means the name of the episode\n" +
            "\"/e\": Means the number of the episode\n" +
            "\"/L\": Means the main target audience language of the episode/movie\n" +
            "\"/T\": Means the type of translation\n" +
            "\"/[\" or \"/]\": Covered in the later paragraph\n" +
            "\"//\": Means literally \"/\"\n" +
            "If those identifiers are found in the given expression, they are replaced by their respective meaning. Every other part of the expression is seen as literal and returned as is.\n" +
            "Characters, which can not be used in the name of a file will be removed. This are mainly Windows specific illegals.\n" +
            "If there is an identifier in the expression, which there is no value for, the identifier by default is left out and simply removed. You can adjust this behavior by using \"/[\" and \"/]\". You can put these identifiers around a segment of the expression. The segment will then only be displayed if every identifier inside that segment can successfully be filled in. If there are missing closing/opening identifier, the identifiers causing the problem are ignored.\n" +
            "In each of these segments you can make use of \"inverted identifiers\". Inverted identifiers make the segment they are in only visible if there exists no value for them. They can be created by putting a \"!\" directly after \"/\" of each identifier. For example \"/[S/sE/e/]/[/!sEpisode /E]\".";

    private final List<Identifier> identifiers;

    EpisodeFormat(String seriesName, String seasonNumber, String episodeNumber, String episodeName,
                  String language, String translationType) {
        this.identifiers = new LinkedList<>();
        identifiers.add(new Identifier("S", seriesName));
        identifiers.add(new Identifier("s", seasonNumber));
        identifiers.add(new Identifier("E", episodeName));
        identifiers.add(new Identifier("e", episodeNumber));
        identifiers.add(new Identifier("L", language));
        identifiers.add(new Identifier("T", translationType));
        identifiers.add(new Literal("//", "/"));
    }

    /**
     * Formats the episodes description to a readable {@code String} using the given expression.
     * <b> Important to read this documentation </b>
     * <p>
     * These are the types of identifiers:<ul>
     * <li>"<b>/S</b>": Means the name of the series
     * <li>"<b>/s</b>": Means the number of the season
     * <li>"<b>/E</b>": Means the name of the episode
     * <li>"<b>/e</b>": Means the number of the episode
     * <li>"<b>/L</b>": Means the main target audience language of the episode
     * <li>"<b>/T</b>": Means the type of translation
     * <li>"<b>/[</b>" or "<b>/]</b>": Covered in the last paragraph
     * <li>"<b>//</b>": Means literally "/"
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
     * They can be created by putting a "!" directly after "/" of each identifier. Does not work with literals! For
     * example "{@code /[S/sE/e/]/[/!sEpisode /E]}" would create "Episode 1" for the first Episode which doesn't have a
     * season number. If it has one, it creates "S1E1" (assuming it's the first season).
     *
     * @param expression a String expression used as template for formatting
     * @return a formatted String representation of an episode
     */
    public String format(String expression) {
        final String regex = "\\/\\[(?<content>[^\\[\\]]*?)\\/\\]";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);

        Matcher matcher = pattern.matcher(expression);
        while (matcher.find()) {
            expression = matcher.replaceAll((match) -> formatSegment(match.group(1)));
            matcher = pattern.matcher(expression);
        }

        String temp = expression.replaceAll("(\\/\\[|\\/\\])", "");
        temp = replaceSimultaneous(temp);

        return temp.replaceAll("[\\\\:*?\"<>|]", "");
    }

    private String formatSegment(String segment) {
        for (Identifier identifier : identifiers) {
            boolean hasValue = identifier.hasValue();
            if (segment.contains(identifier.getIdentifier()) && !hasValue || segment.contains(identifier.getNegativeIdentifier()) && hasValue) {
                return "";
            }
        }

        return replaceSimultaneous(segment);
    }

    private String replaceSimultaneous(String input) {
        String regexp = identifiers.stream().map(Identifier::getReplaceRegex).collect(Collectors.joining("|"));
        Map<String, String> replacements = new HashMap<>();
        identifiers.forEach(identifier -> {
            replacements.put(identifier.getIdentifier(), identifier.getValue());
            replacements.put(identifier.getNegativeIdentifier(), identifier.getValue());
        });

        StringBuffer sb = new StringBuffer();
        Pattern p = Pattern.compile(regexp);
        Matcher m = p.matcher(input);

        while (m.find()) {
            m.appendReplacement(sb, replacements.get(m.group()));
        }
        m.appendTail(sb);

        return sb.toString();
    }

    private static class Identifier {
        private final String identifier, negativeIdentifier, value;

        private Identifier(String key, String value) {
            this("/" + key, "/!" + key, value);
        }

        private Identifier(String identifier, String negativeIdentifier, String value) {
            this.identifier = identifier;
            this.negativeIdentifier = negativeIdentifier;
            this.value = value;
        }

        protected boolean hasValue() {
            return value != null;
        }

        protected String getReplaceRegex() {
            return "(" + Pattern.quote(getIdentifier()) + "|" +
                    Pattern.quote(getNegativeIdentifier()) + ")";
        }

        protected String getIdentifier() {
            return identifier;
        }

        protected String getNegativeIdentifier() {
            return negativeIdentifier;
        }

        protected String getValue() {
            return (value != null ? value : "");
        }
    }

    private static class Literal extends Identifier {
        private Literal(@Nonnull String key, @Nonnull String value) {
            super(key, String.valueOf(Character.MIN_VALUE), value);
        }

        @Override
        protected boolean hasValue() {
            return true;
        }

        @Override
        protected String getReplaceRegex() {
            return "(" + Pattern.quote(getIdentifier()) + ")";
        }
    }
}
