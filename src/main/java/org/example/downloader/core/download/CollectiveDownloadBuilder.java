package org.example.downloader.core.download;

import org.example.downloader.core.format.EpisodeFormat;
import org.example.downloader.core.framework.Downloader;
import org.example.downloader.core.framework.Series;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

//TODO: Implement with good quality (right now only to get alpha release)
public class CollectiveDownloadBuilder {
    private final static String DEFAULT_FORMAT_DOWNLOAD = "/S - /[S/sE/e /[/E/]/]/[/!sEpisode /e/]";
    private final static String DEFAULT_FORMAT_SUBDIR = "/S/[" + (File.separator.equals("/") ? "//" : File.separator) + "Season /s/]";

    private final Set<? extends Series> seriesSet;
    private final Set<? extends Download> currentDownloads;
    private Set<? extends Downloader> downloaderSet; //TODO: Remove

    private int maxDownloads;

    private String formatSubDir, formatDownload;

    /**
     * Creates a {@code CollectiveDownloadBuilder} object, which can be used to
     * download multiple {@code Episode}s/{@code Series}.
     * <p>
     * If the amount of {@code Series} in the given {@code Set} is 1, by default
     * no subdirectories will be created.
     *
     * @param seriesSet a {@code Set} of {@link Series} which should be downloaded
     */
    public CollectiveDownloadBuilder(Set<? extends Series> seriesSet) {
        this.seriesSet = seriesSet;
        this.currentDownloads = new HashSet<>();
        this.maxDownloads = maxDownloads;
        setFormatSubDir(null); //Generate Default for Subdirectory creation
        formatDownload = DEFAULT_FORMAT_DOWNLOAD;
    }

    /**
     * Creates a {@code CollectiveDownloadBuilder} object, which can be used to
     * download multiple {@code Episode}s/{@code Series}.
     * If the amount of {@code Series} in the given varargs is 1, by default
     * no subdirectories will be created.
     *
     * @param series {@link Series} which should be downloaded
     */
    public CollectiveDownloadBuilder(Series... series) {
        this(new HashSet<>(Arrays.asList(series)));
    }


    /**
     * Creates a {@code CollectiveDownloadBuilder} object, which can be used to
     * download multiple {@code Episode}s/{@code Series}.
     *
     * @param downloaders {@link Downloader}(s) which should be downloaded
     */
    public CollectiveDownloadBuilder(Downloader... downloaders) {
        this(new Series((URL) null) {
            @Override
            protected Set<? extends Downloader> generateEpisodeDownloaders() {
                return new HashSet<>(Arrays.asList(downloaders));
            }

            @Override
            public String getInvalidSeriesMessage() {
                return "No Series could be constructed from Downloaders given to CollectiveDownload";
            }
        });
    }

    public void execute(String path, String format) {
        LinkedHashSet<Download> current = new LinkedHashSet<>();
        Queue<Downloader> queued = new LinkedList<>();
        final Map<Download, String> formatMap = new HashMap<>();
        AtomicInteger finished = new AtomicInteger();

        queued.addAll(downloaderSet);

        long time = System.currentTimeMillis();
        while (!(queued.isEmpty() && current.isEmpty())) {
            synchronized (current) {
                if (current.size() < maxDownloads) {
                    boolean validDownload = false;
                    while (!validDownload) {
                        Downloader downloader;
                        synchronized (queued) {
                            downloader = queued.poll();
                        }

                        EpisodeFormat episodeFormat;

                        try (Download download = downloader.generateVideoDownload(path + File.separator + (episodeFormat = downloader.generateEpisodeFormat()).format("/S"), format)) {


                            if (download != null) {
                                String formatted = episodeFormat.format(format);
                                synchronized (queued) {
                                    queued.remove(downloader);
                                    formatMap.put(download, formatted);

                                    if (!download.isAlreadyDownloaded()) {
                                        current.add(download);
                                        validDownload = true;
                                        Download finalDownload = download;
                                        synchronized (System.out) {
                                            System.out.println();
                                        }
                                        download.startParallel((success) -> {
                                            synchronized (current) {
                                                synchronized (System.out) {
                                                    System.out.print("\033[" + (current.size() - 1) + "A\033[2K\rDownload: " + formatMap.get(finalDownload) + (success ? " is finished!" : " failed"));
                                                    System.out.print("\033[" + (current.size() - 1) + "B");
                                                }
                                                current.remove(finalDownload);
                                            }
                                            if (success) {
                                                synchronized (finished) {
                                                    finished.getAndIncrement();
                                                }
                                                try {
                                                    finalDownload.close();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                synchronized (queued) {
                                                    queued.add(downloader);
                                                }
                                            }
                                        });
                                    } else if (download.isAlreadyDownloaded()) {
                                        synchronized (finished) {
                                            finished.getAndIncrement();
                                        }
                                    }
                                }
                                download.close();
                            }
                        } catch (NullPointerException | IndexOutOfBoundsException | IOException e) {
                            //System.out.print(downloader.pageURL + ": ");
                            //e.printStackTrace();
                        }
                    }
                }
            }

            synchronized (current) {
                Download[] downloads = current.toArray(Download[]::new);

                synchronized (System.out) {

                    for (int i = 0; i < downloads.length; i++) {
                        if (i < 1) {
                            System.out.print("\033[" + (current.size() - 1) + "A\033[2K\r");
                        } else {
                            System.out.print("\033[" + 1 + "B\033[2K\r");
                        }
                        System.out.print("Download: " + formatMap.get(downloads[i]) + " " + Math.round(((double) downloads[i].getDownloaded() / downloads[i].getSize()) * 1000D) / 10D + "%");
                    }
                    synchronized (finished) {
                        System.out.print(" | " + finished + "/" + downloaderSet.size() + " Downloads Completed");
                    }
                }
            }

            long diff = (System.currentTimeMillis() - time);
            if (diff < 1000) {
                try {
                    Thread.sleep(1000 - diff);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            time = System.currentTimeMillis();
        }

        System.out.println("Finished!");
    }

    /**
     * Sets the formatting of subdirectories.
     * <p>
     * The parameter controls the creation of subdirectories for every downloaded
     * episode/video. If the parameter is an empty string, no subdirectories
     * will be created. If however the parameter is null, the default subdirectory creation
     * will be used. The parameter is a formatting according to {@link EpisodeFormat#format(String)}.
     *
     * @param formatting empty -> no subdirectories<br>
     *                   null -> {@link CollectiveDownloadBuilder#DEFAULT_FORMAT_SUBDIR}<br>
     *                   everything else -> subdirectory with the given formatting
     */
    public void setFormatSubDir(String formatting) {
        formatSubDir = Objects.requireNonNullElseGet(formatting, () -> (seriesSet.size() == 1 ? DEFAULT_FORMAT_SUBDIR.replaceAll(Pattern.quote("/S"), "") : DEFAULT_FORMAT_SUBDIR));
    }

    /**
     * Sets the formatting of each download.
     * <p>
     * The parameter controls the naming scheme for every downloaded
     * episode/video. If the parameter is an empty string or null, the
     * default naming scheme will be used. The parameter is a formatting
     * according to {@link EpisodeFormat#format(String)}.
     *
     * @param formatting null or empty -> {@link CollectiveDownloadBuilder#DEFAULT_FORMAT_DOWNLOAD}<br>
     *                   everything else -> naming scheme according to the given format
     */
    public void setFormatDownload(String formatting) {
        if (formatting == null || formatting.equals("")) {
            formatDownload = DEFAULT_FORMAT_DOWNLOAD;
        } else
            formatDownload = formatting;
    }
}
