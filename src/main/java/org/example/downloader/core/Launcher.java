package org.example.downloader.core;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.example.downloader.graphics.GUI;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import static org.example.downloader.utils.Utils.*;

public class Launcher {
    public final static Option HELP = new Option(null, "help", false, "shows this");
    public final static Option NSFW = new Option("x", "nsfw", false, "display NSFW hosts in GUI\nif you want to show GUI you need pass only this or no argument");
    public final static Option HOST = new Option("h", "host", true, "the host to download from:");
    public final static Option SERIES = new Option("s", "series", true, "the full URL to a series");
    public final static Option DOWNLOADER = new Option("b", "download", true, "the full URL to a single video");
    public final static Option ALL = new Option("a", "all", false, "download every single video from the host");
    public final static Option DESTINATION_DIRECTORY = new Option("d", "destination", true, "the download destination folder");
    public final static Option MAX_DOWNLOADS = new Option("m", "max-threads", true, "the maximum amount of downloads being executed at the same time");
    public final static Option DOWNLOAD_FORMAT = new Option("f", "format", true, "formats every single downloaded video according to the guidelines shown " +
            "at the end of this output");
    public final static Option SUBDIR_FORMAT = new Option(null, "subdir-format", true, "controls subdirectory creation according to the guidelines shown at the end " +
            "of this output");

    //TODO: Add complete Documentation (JavaDoc) to framework package before release of 0.2.0
    public static void main(String[] args) throws MalformedURLException {
        CommandLine cmd = null;
        ParseException ex = null;
        try {
            cmd = parseOptions(args);
        } catch (ParseException e) {
            ex = e;
        }

        if (cmd == null) {
            List<String> arguments = Arrays.asList(args);
            boolean printHelp = false;
            boolean successGUI = true;

            if (arguments.contains("--" + HELP.getLongOpt())) {
                printHelp = true;
                successGUI = false;
            } else if (arguments.isEmpty() || arguments.get(0).equals("-" + NSFW.getOpt()) || arguments.get(0).equals("--" + NSFW.getLongOpt())) {
                try {
                    if (arguments.isEmpty()) GUI.start();
                    else GUI.start(HELP.getLongOpt());
                } catch (RuntimeException ignored) {
                    successGUI = false;
                }
            }

            if (!printHelp) {
                if (!successGUI) {
                    System.out.println("GUI could not be started correctly. Please refer to following help page as alternative:\n");
                } else {
                    System.out.println(ex.getMessage() + "\n");
                }
            }
            if (!successGUI && printHelp) printHelp();
            System.exit((printHelp || successGUI ? 0 : 1));
        } else {
            initializeSetup();


            System.exit(0);
        }
    }
}
