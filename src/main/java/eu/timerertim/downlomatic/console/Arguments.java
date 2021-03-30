package eu.timerertim.downlomatic.console;

import eu.timerertim.downlomatic.pages.Hosts;
import org.apache.commons.cli.Option;

import java.util.function.Consumer;

/**
 * This enum stores every possible argument passable via CLI.
 * <p>
 * Under the hood, it works with {@link Option}s.
 */
public enum Arguments {
    NO_FILE_LOGGING(new Option(null, "no-file-logging", false, "disables logging to file"), true),
    DESTINATION_DIRECTORY(new Option("d", "destination", true, "the download destination folder"),
            option -> {
                option.setRequired(true);
                option.setArgName("directory");
            }),
    HOST(new Option("h", "host", true, "the host to download from:"),
            option -> {
                option.setRequired(true);
                option.setArgName("host");

                // List HOSTS available
                final Hosts[] values = Hosts.values();
                for (int i = 0; i < values.length; i++) {
                    option.setDescription(option.getDescription() + (i > 0 ? ", " : " ") + values[i].name());
                }
            }),
    SERIES(new Option("s", "series", true, "the full URL to a series"),
            option -> {
                option.setArgName("url");
            }),
    DOWNLOADER(new Option("b", "download", true, "the full URL to a single video"),
            option -> {
                option.setArgName("url");
            }),
    ALL(new Option("a", "all", false, "download every single video from the host")),
    MAX_DOWNLOADS(new Option("t", "threads", true, "the maximum amount of downloads being executed at the same time"),
            option -> {
                option.setArgName("amount");
            }),
    DOWNLOAD_FORMAT(new Option("f", "format", true, "the format of every single downloaded video"),
            option -> {
                option.setArgName("formatting");
            }),
    SUBDIR_FORMAT(new Option(null, "subdir-format", true, "the format of every subdirectory created\n" +
            "by default, subdirectories will only be created with the --" + ALL.option.getLongOpt() + " flag"),
            option -> {
                option.setArgName("formatting");
            }),
    NSFW(new Option("x", "nsfw", false, "display NSFW hosts in GUI\n" +
            "if you want to show GUI you need pass only this or no argument")),
    HELP(new Option(null, "help", false, "shows this"));

    private final Option option;
    private final boolean isHidden;

    Arguments(Option option) {
        this(option, false);
    }

    Arguments(Option option, boolean hidden) {
        this(option, (empty) -> {
        }, hidden);
    }

    Arguments(Option option, Consumer<Option> conf) {
        this(option, conf, false);
    }

    Arguments(Option option, Consumer<Option> conf, boolean hidden) {
        this.option = option;
        conf.accept(option);
        isHidden = hidden;
    }

    /**
     * Returns the {@code Option} this {@code Argument} wraps.
     *
     * @return the option
     */
    public Option getOption() {
        return option;
    }

    /**
     * Returns true if the option should not be displayed in the help screen.
     *
     * @return if the option is hidden
     */
    boolean isHidden() {
        return isHidden;
    }
}
