package eu.timerertim.downlomatic.core;

import eu.timerertim.downlomatic.core.download.CollectiveDownloadBuilder;
import eu.timerertim.downlomatic.core.framework.Host;
import eu.timerertim.downlomatic.graphics.GUI;
import eu.timerertim.downlomatic.pages.Hosts;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static eu.timerertim.downlomatic.utils.Utils.*;

public class Launcher {
    public final static Option HELP = new Option(null, "help", false, "shows this");
    public final static Option NSFW = new Option("x", "nsfw", false, "display NSFW hosts in GUI\nif you want to show GUI you need pass only this or no argument");
    public final static Option DESTINATION_DIRECTORY = new Option("d", "destination", true, "the download destination folder");
    public final static Option HOST = new Option("h", "host", true, "the host to download from:");
    public final static Option SERIES = new Option("s", "series", true, "the full URL to a series");
    public final static Option DOWNLOADER = new Option("b", "download", true, "the full URL to a single video");
    public final static Option ALL = new Option("a", "all", false, "download every single video from the host");
    public final static Option MAX_DOWNLOADS = new Option("m", "max-threads", true, "the maximum amount of downloads being executed at the same time");
    public final static Option DOWNLOAD_FORMAT = new Option("f", "format", true, "formats every single downloaded video according to the guidelines shown " +
            "at the end of this output");
    public final static Option SUBDIR_FORMAT = new Option(null, "subdir-format", true, "controls subdirectory creation according to the guidelines shown at the end " +
            "of this output");

    //TODO: Add complete Documentation (JavaDoc) to framework package before release of 0.2.0
    public static void main(String[] args) {
        CommandLine cmd = null;
        ParseException ex = null;
        try {
            cmd = parseOptions(args);
        } catch (ParseException e) {
            ex = e;
        }

        if (cmd == null) {
            List<String> arguments = Arrays.asList(args);

            if (arguments.contains("--" + HELP.getLongOpt())) {
                printHelp();
                System.exit(0);
            } else if (arguments.isEmpty() || arguments.get(0).equals("-" + NSFW.getOpt()) || arguments.get(0).equals("--" + NSFW.getLongOpt())) {
                try {
                    if (arguments.isEmpty()) GUI.start();
                    else GUI.start(NSFW.getLongOpt());
                    System.exit(0);
                } catch (RuntimeException e) {
                    System.out.println("GUI experienced problems. Please refer to following help page as alternative:\n");
                }
            } else {
                System.out.println(ex.getMessage() + "\n");
            }

            printHelp();
            System.exit(1);
        } else {
            launchCLI(cmd);
        }
    }

    private static void launchCLI(CommandLine cmd) {
        CollectiveDownloadBuilder builder = null;
        Host host = null;
        Integer max = null;
        String downloadFormat = null, subdirFormat = null;

        try {
            host = Hosts.valueOf(cmd.getOptionValue(HOST.getOpt()).toUpperCase()).getHost();
        } catch (IllegalArgumentException e) {
            System.out.println("Illegal argument: host has to be a valid type\n");
            printHelp();
            System.exit(1);
        }
        if (cmd.hasOption(MAX_DOWNLOADS.getOpt())) {
            try {
                max = Integer.parseInt(cmd.getOptionValue(MAX_DOWNLOADS.getOpt()));
            } catch (IllegalArgumentException e) {
                System.out.println("Illegal argument: max downloads has to be a number");
                printHelp();
                System.exit(1);
            }
        }
        if (cmd.hasOption(DOWNLOAD_FORMAT.getOpt())) {
            downloadFormat = cmd.getOptionValue(DOWNLOAD_FORMAT.getOpt());
        }
        if (cmd.hasOption(SUBDIR_FORMAT.getLongOpt())) {
            subdirFormat = cmd.getOptionValue(SUBDIR_FORMAT.getLongOpt());
        }

        initializeSetup();

        try {
            String destinationPath = cmd.getOptionValue(DESTINATION_DIRECTORY.getOpt());
            String url;
            if (cmd.hasOption(ALL.getOpt())) {
                builder = new CollectiveDownloadBuilder(destinationPath, host.getSeries());
            } else if ((url = cmd.getOptionValue(SERIES.getOpt())) != null) {
                builder = new CollectiveDownloadBuilder(destinationPath, host.getSeries(new URL(url)));
            } else if ((url = cmd.getOptionValue(DOWNLOADER.getOpt())) != null) {
                builder = new CollectiveDownloadBuilder(destinationPath, host.getDownloader(new URL(url)));
            } else {
                onExit();
                System.out.println("Missing argument: -a | -s | -d\n");
                printHelp();
                System.exit(1);
            }
        } catch (MalformedURLException e) {
            onExit();
            System.out.println("Illegal argument: " + e.getMessage() + "\n");
            printHelp();
            System.exit(1);
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
    }
}
