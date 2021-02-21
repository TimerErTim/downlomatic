package org.example.downloader.core.download;

import org.example.downloader.core.format.EpisodeFormat;
import org.example.downloader.core.framework.Downloader;
import org.example.downloader.core.framework.Series;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class CollectiveDownload {
    private final static long CHECK_INTERVAL = 500;

    private final Set<? extends Series> seriesSet;
    private final String path, formatSubDir, formatDownload;
    private final int maxDownloads;

    private final Queue<Series> seriesQueue;
    private final Queue<Downloader> downloaderQueue;
    private final Queue<Download> downloadQueue;
    private final Map<Download, String> downloadNameMap;
    private final Set<Download> currentDownloads;

    private final AtomicInteger finishedDownloads;
    private State state;

    private long slowModeDelay;

    CollectiveDownload(Set<? extends Series> seriesSet, String path, String formatSubDir, String formatDownload, int maxDownloads) {
        this.path = path;
        this.formatSubDir = formatSubDir;
        this.formatDownload = formatDownload;
        this.maxDownloads = maxDownloads;
        this.seriesSet = seriesSet;
        seriesQueue = new LinkedBlockingQueue<>();
        downloaderQueue = new LinkedBlockingQueue<>();
        downloadQueue = new LinkedBlockingQueue<>();
        downloadNameMap = new ConcurrentHashMap<>();
        currentDownloads = ConcurrentHashMap.newKeySet(maxDownloads);
        finishedDownloads = new AtomicInteger();
        slowModeDelay = 0;
        state = State.IDLE;

        seriesQueue.addAll(seriesSet);
    }

    //TODO: Implement download() with JavaDoc
    @SuppressWarnings("BusyWait")
    public void download() {
        if (state == State.IDLE || state == State.PAUSED) {
            state = State.RUNNING;

            Thread downloaderParseThread = new Thread(() -> {
                while (!seriesQueue.isEmpty() && !Thread.interrupted()) {
                    Series series = seriesQueue.peek();

                    try {
                        if (series.isEmpty()) {
                            series.fillDownloaders();
                            Thread.sleep(slowModeDelay);
                        }
                    } catch (MalformedURLException e) {
                        System.out.println(e.getMessage());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        for (Downloader downloader : series) {
                            downloaderQueue.add(downloader);
                        }
                        seriesQueue.remove();
                    }
                }
            });

            Thread downloadParseThread = new Thread(() -> {
                while (!(seriesQueue.isEmpty() && downloaderQueue.isEmpty())
                        && !Thread.interrupted()) {
                    if (downloaderQueue.isEmpty()) {
                        try {
                            Thread.sleep(CHECK_INTERVAL);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    } else {
                        Downloader downloader = downloaderQueue.peek();

                        if (downloader != null) {
                            try {
                                if (downloader.getVideoDownload() == null) {
                                    Thread.sleep(slowModeDelay);
                                }

                                EpisodeFormat format = downloader.getEpisodeFormat();
                                Download download = downloader.generateVideoDownload(path + File.separator + format.format(formatSubDir), formatDownload);
                                downloadNameMap.put(download, format.format(formatDownload));
                                downloadQueue.add(download);
                            } catch (MalformedURLException e) {
                                System.out.println(e.getMessage());
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            } finally {
                                downloaderQueue.remove();
                            }
                        }
                    }
                }
            });

            Thread downloadManageThread = new Thread(() -> {
                currentDownloads.forEach((Download::startParallel));

                while (!(seriesQueue.isEmpty() && downloaderQueue.isEmpty() && downloadQueue.isEmpty())
                        && !Thread.interrupted()) {
                    if (downloadQueue.isEmpty() || currentDownloads.size() >= maxDownloads) {
                        try {
                            Thread.sleep(CHECK_INTERVAL);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    } else {
                        Download download = downloadQueue.peek();

                        if (download != null) {
                            if (download.isAlreadyDownloaded()) {
                                finishedDownloads.getAndIncrement();
                            } else {
                                currentDownloads.add(download);
                                System.out.println();
                                download.startParallel((success) -> {
                                    synchronized (currentDownloads) {
                                        synchronized (System.out) {
                                            System.out.print("\033[" + (currentDownloads.size() - 1) + "A\033[2K\rDownload: " + downloadNameMap.get(download) + (success ? " is finished!" : " failed"));
                                            System.out.print("\033[" + (currentDownloads.size() - 1) + "B");
                                        }
                                    }

                                    if (success) {
                                        finishedDownloads.getAndIncrement();
                                    } else {
                                        downloadQueue.add(download);
                                    }
                                    currentDownloads.remove(download);
                                });
                            }
                        }

                        downloadQueue.remove();
                    }
                }
            });

            Thread downloadDisplayThread = new Thread(() -> {
                while (!(seriesQueue.isEmpty() && downloaderQueue.isEmpty() && downloadQueue.isEmpty() && currentDownloads.isEmpty())
                        && !Thread.interrupted()) {
                    Download[] downloads = currentDownloads.toArray(Download[]::new);

                    synchronized (System.out) {
                        for (int i = 0; i < downloads.length; i++) {
                            if (i < 1) {
                                System.out.print("\033[" + (downloads.length - 1) + "A\033[2K\r");
                            } else {
                                System.out.print("\033[" + 1 + "B\033[2K\r");
                            }
                            System.out.print(downloadNameMap.get(downloads[i]) + ": " + Math.round(((double) downloads[i].getDownloaded() / downloads[i].getSize()) * 1000D) / 10D + "%");
                        }
                        System.out.print(" | " + finishedDownloads + " Downloads Completed");
                    }
                }

                if (seriesQueue.isEmpty() && downloaderQueue.isEmpty() && downloadQueue.isEmpty() && currentDownloads.isEmpty()) {
                    System.out.println("Finished!");
                    state = State.FINISHED;
                }
            });

            downloaderParseThread.start();
            downloadParseThread.start();
            downloadManageThread.start();
            downloadDisplayThread.start();
        }
    }

    //TODO: Implement stop() with JavaDoc
    public void stop() {

    }

    //TODO: Implement pause() with JavaDoc
    public void pause() {

    }

    /**
     * Enables slow mode for this {@code CollectiveDownloadBuilder}.
     * <p>
     * The slow mode determines the minimum delay between fetching downloads
     * from the web rather than the download speed. This is useful
     * for websites with bot/DDoS protection.
     * <p>
     * The interval is one fetch every given amount of milliseconds.
     *
     * @param slowModeDelay the delay between each web request
     */
    public void setSlowModeDelay(long slowModeDelay) {
        if (slowModeDelay >= 0)
            this.slowModeDelay = slowModeDelay;
        else {
            this.slowModeDelay = 0;
        }
    }

    /**
     * Enables slow mode for this {@code CollectiveDownloadBuilder}.
     * <p>
     * The slow mode determines the minimum delay between fetching downloads
     * from the web rather than the download speed. This is useful
     * for websites with bot/DDoS protection.
     * <p>
     * The default interval is once every 500ms.
     */
    public void enableSlowMode() {
        if (this.slowModeDelay <= 0) {
            this.slowModeDelay = 500;
        }
    }

    /**
     * Disables slow mode for this {@code CollectiveDownloadBuilder}.
     * <p>
     * For more information consult {@link CollectiveDownload#enableSlowMode()}.
     */
    public void disableSlowMode() {
        this.slowModeDelay = 0;
    }

    /**
     * Returns the current {@code State} of this CollectiveDownload.
     *
     * @return the current {@code State}
     */
    public State getState() {
        return state;
    }

    public enum State {
        IDLE, RUNNING, PAUSED, FINISHED
    }
}
