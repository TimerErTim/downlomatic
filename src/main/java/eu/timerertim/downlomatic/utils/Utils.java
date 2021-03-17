package eu.timerertim.downlomatic.utils;

import com.sun.jna.Function;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import eu.timerertim.downlomatic.core.format.EpisodeFormat;
import eu.timerertim.downlomatic.pages.Hosts;
import org.apache.commons.cli.*;

import static eu.timerertim.downlomatic.core.Launcher.*;

/**
 * Provides some neutral helper functions and helps in code reuse.
 */
public class Utils {
    private static Options options;
    private static HelpFormatter help;

    /**
     * Initializes stuff which is needed for
     * successful execution.
     */
    public static void initializeSetup() {
        WebScrapers.initialize();

        // Windows 10 VT100 Terminal - copied from Stackoverflow
        if (System.getProperty("os.name").startsWith("Windows")) {
            try {
                // Set output mode to handle virtual terminal sequences
                Function GetStdHandleFunc = Function.getFunction("kernel32", "GetStdHandle");
                DWORD STD_OUTPUT_HANDLE = new DWORD(-11);
                HANDLE hOut = (HANDLE) GetStdHandleFunc.invoke(HANDLE.class, new Object[]{STD_OUTPUT_HANDLE});

                DWORDByReference p_dwMode = new DWORDByReference(new DWORD(0));
                Function GetConsoleModeFunc = Function.getFunction("kernel32", "GetConsoleMode");
                GetConsoleModeFunc.invoke(BOOL.class, new Object[]{hOut, p_dwMode});

                int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4;
                DWORD dwMode = p_dwMode.getValue();
                dwMode.setValue(dwMode.intValue() | ENABLE_VIRTUAL_TERMINAL_PROCESSING);
                Function SetConsoleModeFunc = Function.getFunction("kernel32", "SetConsoleMode");
                SetConsoleModeFunc.invoke(BOOL.class, new Object[]{hOut, dwMode});
            } catch (Exception e) {
                System.out.println("VT100 Emulation could not be activated for Windows OS: CLI presentation will look like bullshit");
            }
        }

    }

    /**
     * Basically the counterpart to {@link Utils#initializeSetup()}.
     * <p>
     * Closes every global resource and performs cleanup on exit.
     */
    public static void onExit() {
        WebScrapers.close();
    }

    /**
     * Parses commandline arguments for easier usage.
     *
     * @param args the commandline arguments
     * @return an abstract presentation
     */
    public static CommandLine parseOptions(String[] args) throws ParseException {
        HELP.setRequired(false);
        FILE_LOGGING.setRequired(false);
        NSFW.setRequired(false);
        HOST.setRequired(true);
        HOST.setArgName("host");
        SERIES.setRequired(false);
        SERIES.setArgName("url");
        DOWNLOADER.setRequired(false);
        DOWNLOADER.setArgName("url");
        ALL.setRequired(false);
        DESTINATION_DIRECTORY.setRequired(true);
        DESTINATION_DIRECTORY.setArgName("directory");
        MAX_DOWNLOADS.setRequired(false);
        MAX_DOWNLOADS.setArgName("threads");
        DOWNLOAD_FORMAT.setRequired(false);
        DOWNLOAD_FORMAT.setArgName("formatting");
        SUBDIR_FORMAT.setRequired(false);
        SUBDIR_FORMAT.setArgName("formatting");

        // List HOSTS in usage
        final Hosts[] values = Hosts.values();
        for (int i = 0; i < values.length; i++) {
            HOST.setDescription(HOST.getDescription() + (i > 0 ? ", " : " ") + values[i].name());
        }

        OptionGroup src = new OptionGroup();
        src.addOption(SERIES).addOption(DOWNLOADER).addOption(ALL);
        src.setRequired(true);

        options = new Options();
        options.addOption(DESTINATION_DIRECTORY).
                addOption(HOST).
                addOptionGroup(src).
                addOption(NSFW).
                addOption(MAX_DOWNLOADS).
                addOption(DOWNLOAD_FORMAT).
                addOption(SUBDIR_FORMAT).
                addOption(HELP);

        CommandLineParser commandLineParser = new DefaultParser();
        help = new HelpFormatter();

        return commandLineParser.parse(options, args);
    }

    /**
     * Prints a help message.
     */
    public static void printHelp() {
        help.printHelp("AnimeDownloader -d <directory> -h <host> -a | -b <url> | -s <url> [-f <formatting>] [--subdir-format <formatting>] [-m <threads>]",
                null, options, "\n" + EpisodeFormat.DESCRIPTION);
    }
}
