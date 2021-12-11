package eu.timerertim.downlomatic.core;

import eu.timerertim.downlomatic.console.Arguments;
import eu.timerertim.downlomatic.console.ConsoleParsedArguments;
import eu.timerertim.downlomatic.console.ConsoleUtils;
import eu.timerertim.downlomatic.core.download.CollectiveDownloadBuilder;
import eu.timerertim.downlomatic.core.framework.Host;
import eu.timerertim.downlomatic.graphics.GUI;
import eu.timerertim.downlomatic.hosts.Hosts;
import eu.timerertim.downlomatic.util.logging.Level;
import eu.timerertim.downlomatic.util.logging.Log;
import org.apache.commons.cli.ParseException;

import java.net.MalformedURLException;
import java.net.URL;

import static eu.timerertim.downlomatic.util.Utils.initializeSetup;
import static eu.timerertim.downlomatic.util.Utils.onExit;

public class Launcher {
    public static void main(String... args) {
        try {
            ConsoleParsedArguments parsedArguments = ConsoleUtils.parseArgs(args);

            int requiredSizeForGUI = 2;
            if (!parsedArguments.hasArgument(Arguments.NO_FILE_LOGGING)) {
                Log.setFileLogging(true);
                requiredSizeForGUI--;
            }
            if (parsedArguments.hasArgument(Arguments.VERBOSE)) {
                Log.setConsoleVerbosity(Level.ALL);
                requiredSizeForGUI++;
            }

            if (parsedArguments.hasArgument(Arguments.HELP)) {
                ConsoleUtils.printHelp();
                System.exit(0);
            }

            if (!parsedArguments.hasRequiredArguments()) {
                try {
                    if (parsedArguments.hasArgument(Arguments.NSFW) && parsedArguments.getSize() == requiredSizeForGUI) {
                        GUI.start(Arguments.NSFW.getOption().getLongOpt());
                    } else if (parsedArguments.getSize() == requiredSizeForGUI - 1) {
                        GUI.start();
                    } else {
                        showErrorHelpMessage(parsedArguments.getMissingArgumentMessage());
                    }
                } catch (RuntimeException e) {
                    Log.w("GUI experienced problem preventing proper execution.");
                    showErrorHelpMessage("GUI experienced problems. Please refer to following help page as alternative:\n");
                }
            } else {
                launchCLI(parsedArguments);
            }
        } catch (ParseException e) {
            showErrorHelpMessage(e.getMessage());
        }
    }

    private static void launchCLI(ConsoleParsedArguments args) {
        CollectiveDownloadBuilder builder = null;
        Host host = null;
        Integer max = null;
        String downloadFormat = null, subdirFormat = null;

        try {
            host = Hosts.valueOf(args.get(Arguments.HOST).toUpperCase()).getHost();
        } catch (IllegalArgumentException e) {
            showErrorHelpMessage("Illegal argument: host has to be a valid type");
        }

        if (args.hasArgument(Arguments.MAX_DOWNLOADS)) {
            try {
                max = Integer.parseInt(args.get(Arguments.HOST));
            } catch (IllegalArgumentException e) {
                showErrorHelpMessage("Illegal argument: max downloads has to be a number");
            }
        }

        if (args.hasArgument(Arguments.DOWNLOAD_FORMAT)) {
            downloadFormat = args.get(Arguments.DOWNLOAD_FORMAT);
        }
        if (args.hasArgument(Arguments.SUBDIR_FORMAT)) {
            subdirFormat = args.get(Arguments.SUBDIR_FORMAT);
        }

        initializeSetup();

        try {
            String destinationPath = args.get(Arguments.DESTINATION_DIRECTORY);
            String url;
            if (args.hasArgument(Arguments.ALL)) {
                builder = new CollectiveDownloadBuilder(destinationPath, host.getSeries());
            } else if ((url = args.get(Arguments.SERIES)) != null) {
                builder = new CollectiveDownloadBuilder(destinationPath, host.getSeries(new URL(url)));
            } else if ((url = args.get(Arguments.DOWNLOADER)) != null) {
                builder = new CollectiveDownloadBuilder(destinationPath, host.getDownloader(new URL(url)));
            } else {
                onExit();
                showErrorHelpMessage("Missing argument: -a | -s | -d\n");
            }
        } catch (MalformedURLException e) {
            onExit();
            showErrorHelpMessage("Illegal argument: " + e.getMessage());
        }

        if (max != null) {
            builder.setMaxDownloads(max);
        }
        if (subdirFormat != null) {
            builder.setFormatSubDir(subdirFormat);
        }
        if (downloadFormat != null) {
            builder.setFormatDownload(downloadFormat);
        }

        builder.onFinish(() -> {
            onExit();
            System.exit(0);
        });

        builder.execute();
    }

    private static void showErrorHelpMessage(String errorMessage) {
        System.out.println(errorMessage + "\n");
        ConsoleUtils.printHelp();
        System.exit(1);
    }
}
