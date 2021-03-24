package eu.timerertim.downlomatic.core.format;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.*;
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
    public static final String WIKI_URL = "https://github.com/TimerErTim/downlomatic/wiki/Formatting";

    private final List<Identifier> identifiers;
    private static final List<Literal> literals = Arrays.asList(
            new Literal("//", File.separator)
    );
    private static final List<Illegal> illegals = Arrays.asList(
            new Illegal("/"),
            new Illegal("\\"),
            new Illegal("["),
            new Illegal("]"),
            new Illegal(":"),
            new Illegal("*"),
            new Illegal("?"),
            new Illegal("\""),
            new Illegal("<"),
            new Illegal(">"),
            new Illegal("|")
    );

    EpisodeFormat(String seriesName, String seasonNumber, String episodeNumber, String episodeName,
                  String language, String translationType) {
        this.identifiers = new LinkedList<>();
        identifiers.add(new Identifier("S", seriesName));
        identifiers.add(new Identifier("s", seasonNumber));
        identifiers.add(new Identifier("E", episodeName));
        identifiers.add(new Identifier("e", episodeNumber));
        identifiers.add(new Identifier("L", language));
        identifiers.add(new Identifier("T", translationType));

        identifiers.addAll(literals);
        identifiers.addAll(illegals);
    }

    private static List<String> findSegments(String input) {
        List<String> segments = new LinkedList<>();
        Stack<String> stack = new Stack<>();
        int startIndex = 0;
        int endIndex;

        int openingIndex;
        int closingIndex;

        int index = 0;
        // Checking for opening and closing brackets with a stack
        for (; ; index++) {
            openingIndex = input.indexOf("/[", index);
            closingIndex = input.indexOf("/]", index);

            if (openingIndex == -1 && closingIndex == -1) {
                break;
            } else if (openingIndex < closingIndex && openingIndex != -1) {
                if (stack.isEmpty()) {
                    startIndex = openingIndex;
                }
                stack.push("/[");
                index = openingIndex;
            } else if (closingIndex < openingIndex || openingIndex == -1) {
                if (!stack.isEmpty()) {
                    stack.pop();
                    if (stack.isEmpty()) {
                        // If the stack is empty, the last bracket must have been reached, so we can save a segment
                        endIndex = closingIndex + 1;
                        segments.add(input.substring(startIndex, endIndex + 1));
                    }
                }
                index = closingIndex;
            }
        }

        return segments;
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
        List<String> subSegments;

        // If the expression is a segment
        if ((subSegments = findSegments(expression)).size() == 1 && subSegments.get(0).equals(expression)) {
            expression = expression.substring(2, expression.length() - 2);
            subSegments = findSegments(expression);

            // Remove inner segments
            List<Literal> replacements = new LinkedList<>();
            for (String s : subSegments) {
                replacements.add(new Illegal(s));
            }
            String temp = replaceSimultaneous(expression, replacements, true);

            // Return empty string according to segment logic
            for (Identifier identifier : identifiers) {
                boolean hasValue = identifier.hasValue();
                if (temp.contains(identifier.getIdentifier()) && !hasValue || temp.contains(identifier.getNegativeIdentifier()) && hasValue) {
                    return "";
                }
            }
        }

        // Calculate replacements for inner segments
        List<Literal> replacements = new LinkedList<>();
        for (String s : subSegments) {
            replacements.add(new Literal(s, format(s)));
        }

        // Return filled in expression
        return replaceSimultaneous(expression, replacements);
    }

    private String replaceSimultaneous(String input, List<? extends Identifier> extra) {
        return replaceSimultaneous(input, extra, false);
    }

    private String replaceSimultaneous(String input, List<? extends Identifier> extra, boolean overwrite) {
        // Create replacement pool
        List<Identifier> identifiers = new LinkedList<>(extra);
        if (!overwrite) {
            identifiers.addAll(this.identifiers);
        }
        // Create replacement regular expression
        String regexp = identifiers.stream().map(Identifier::getReplaceRegex).collect(Collectors.joining("|"));
        if (regexp.isEmpty()) {
            // Return input as is if there's nothing to replace
            return input;
        } else {
            // Create replacement map out of replacement pool
            Map<String, String> replacements = new HashMap<>();
            identifiers.forEach(identifier -> {
                replacements.put(identifier.getIdentifier(), identifier.getValue());
                replacements.put(identifier.getNegativeIdentifier(), "");
            });

            // Replace everything "simultaneously"
            StringBuffer sb = new StringBuffer();
            Pattern p = Pattern.compile(regexp);
            Matcher m = p.matcher(input);

            while (m.find()) {
                m.appendReplacement(sb, replacements.get(m.group()));
            }
            m.appendTail(sb);

            return sb.toString();
        }
    }

    private static class Identifier {
        private final String identifier, negativeIdentifier, value;

        public Identifier(String key, String value) {
            this("/" + key, "/!" + key,
                    value != null ? value.replaceAll(
                            illegals.stream().map(Identifier::getReplaceRegex).collect(Collectors.joining("|")),
                            "") : null);
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
        public Literal(@Nonnull String key, @Nonnull String value) {
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

    private static class Illegal extends Literal {
        public Illegal(@NotNull String key) {
            super(key, "");
        }
    }
}
