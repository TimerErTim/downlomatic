package org.example.downloader.core.download;

import org.example.downloader.core.format.EpisodeFormat;
import org.example.downloader.core.framework.Downloader;
import org.example.downloader.core.framework.Series;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CollectiveDownload {
    private final static long CHECK_INTERVAL = 500;

    private final Set<? extends Series> seriesSet;
    private final String path, formatSubDir, formatDownload;
    private final int maxDownloads;
    private final Runnable onFinish;

    private final Queue<Series> seriesQueue;
    private final Queue<Downloader> downloaderQueue;
    private final Queue<Download> downloadQueue;
    private final Set<Download> currentDownloads;
    private final AtomicInteger finishedDownloads;
    private final AtomicLong slowModeDelay;
    private Thread downloaderParseThread;
    private Thread downloadParseThread;
    private Thread downloadManageThread;

    private State state;
    private Thread downloadDisplayThread;

    CollectiveDownload(Set<? extends Series> seriesSet, String path, String formatSubDir, String formatDownload, int maxDownloads, Runnable onFinish) {
        this.seriesSet = seriesSet;
        this.path = path;
        this.formatSubDir = formatSubDir;
        this.formatDownload = formatDownload;
        this.maxDownloads = maxDownloads;
        this.onFinish = onFinish;
        seriesQueue = new LinkedBlockingQueue<>();
        downloaderQueue = new LinkedBlockingQueue<>();
        downloadQueue = new LinkedBlockingQueue<>();
        currentDownloads = ConcurrentHashMap.newKeySet(maxDownloads);
        finishedDownloads = new AtomicInteger();
        slowModeDelay = new AtomicLong();
        state = State.IDLE;

        seriesQueue.addAll(seriesSet);
    }

    /**
     * Downloads every single possible file in this {@code CollectiveDownload}.
     * <p>
     * This method is not blocking and can only be started if the {@code CollectiveDownload}
     * is {@link State#IDLE} or {@link State#PAUSED}.
     */
    @SuppressWarnings("BusyWait")
    public void download() {
        if (state == State.IDLE || state == State.PAUSED) {
            state = State.RUNNING;

            downloaderParseThread = new Thread(() -> {
                while (!seriesQueue.isEmpty() && !Thread.interrupted()) {
                    Series series = seriesQueue.peek();

                    try {
                        if (series.isEmpty()) {
                            series.fillDownloaders();
                            Thread.sleep(slowModeDelay.get());
                        }
                    } catch (MalformedURLException e) {
                        System.out.println("\n" + e.getMessage());
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

            downloadParseThread = new Thread(() -> {
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
                                    Thread.sleep(slowModeDelay.get());
                                }

                                EpisodeFormat format = downloader.getEpisodeFormat();
                                Download download = downloader.generateVideoDownload(path + File.separator + format.format(formatSubDir), formatDownload);
                                downloadQueue.add(download);
                            } catch (MalformedURLException e) {
                                System.out.println("\n" + e.getMessage());
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            } finally {
                                downloaderQueue.remove();
                            }
                        }
                    }
                }
            });

            downloadManageThread = new Thread(() -> {
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
                                            System.out.print("\033[" + (currentDownloads.size() - 1) + "A\033[2K\rDownload: " + download.getFileName() + (success ? " is finished!" : " failed"));
                                            System.out.print("\033[" + (currentDownloads.size() - 1) + "B");
                                            currentDownloads.remove(download);
                                        }
                                    }

                                    if (success) {
                                        finishedDownloads.getAndIncrement();
                                    }
                                });
                            }
                        }

                        downloadQueue.remove();
                    }
                }
            });

            downloadDisplayThread = new Thread(() -> {
                while (!(seriesQueue.isEmpty() && downloaderQueue.isEmpty() && downloadQueue.isEmpty() && currentDownloads.isEmpty())
                        && !Thread.interrupted()) {

                    synchronized (System.out) {
                        Download[] downloads = currentDownloads.toArray(Download[]::new);
                        for (int i = 0; i < downloads.length; i++) {
                            if (i < 1) {
                                System.out.print("\033[" + (downloads.length - 1) + "A\033[2K\r");
                            } else {
                                System.out.print("\033[" + 1 + "B\033[2K\r");
                            }
                            System.out.print(downloads[i].getFileName() + ": " + Math.round(((double) downloads[i].getDownloaded() / downloads[i].getSize()) * 1000D) / 10D + "%");
                            if (i == downloads.length - 1) {
                                System.out.print(" | " + finishedDownloads + " Downloads Completed");
                            }
                        }
                    }

                    try {
                        Thread.sleep(CHECK_INTERVAL);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                if (seriesQueue.isEmpty() && downloaderQueue.isEmpty() && downloadQueue.isEmpty() && currentDownloads.isEmpty()) {
                    System.out.println("\nFinished!");
                    state = State.FINISHED;
                    onFinish.run();
                }
            });

            downloaderParseThread.start();
            downloadParseThread.start();
            downloadManageThread.start();
            downloadDisplayThread.start();
        }
    }

    /**
     * Stops the currently running {@code CollectiveDownload}.
     * <p>
     * This method is blocking until all background {@code Thread}s were
     * terminated. Has no effect if this {@code CollectiveDownload} is
     * not running. The {@link State} of this {@code CollectiveDownload}
     * can not be changed after invoking this method by invoking {@link CollectiveDownload#pause()}.
     */
    public void stop() {
        if (state == State.RUNNING) {
            pause();

            for (Download download : currentDownloads) {
                download.stop();
            }

            currentDownloads.clear();
            downloadQueue.clear();
            downloaderQueue.clear();
            seriesQueue.clear();
            seriesQueue.addAll(seriesSet);
            finishedDownloads.set(0);

            state = State.IDLE;
        }
    }

    /**
     * Pauses the currently running {@code CollectiveDownload}.
     * <p>
     * This method is blocking until all background {@code Thread}s were
     * terminated. Has no effect if this {@code CollectiveDownload} is
     * not running. The {@link State} of this {@code CollectiveDownload}
     * can not be changed after invoking this method by invoking {@link CollectiveDownload#stop()}.
     */
    public void pause() {
        if (state == State.RUNNING) {
            downloadDisplayThread.interrupt();
            downloadManageThread.interrupt();
            downloadParseThread.interrupt();
            downloaderParseThread.interrupt();

            try {
                downloadDisplayThread.join();
                downloadManageThread.join();
                downloadParseThread.join();
                downloaderParseThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            for (Download download : currentDownloads) {
                download.pause();
            }

            state = State.PAUSED;
        }
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
            this.slowModeDelay.set(slowModeDelay);
        else {
            this.slowModeDelay.set(0);
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
        if (this.slowModeDelay.get() <= 0) {
            this.slowModeDelay.set(500);
        }
    }

    /**
     * Disables slow mode for this {@code CollectiveDownloadBuilder}.
     * <p>
     * For more information consult {@link CollectiveDownload#enableSlowMode()}.
     */
    public void disableSlowMode() {
        this.slowModeDelay.set(0);
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
