package org.example.downloader.core.download;

import org.example.downloader.core.format.EpisodeFormat;
import org.example.downloader.core.framework.Downloader;
import org.example.downloader.core.framework.Series;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class CollectiveDownload {
    private final Set<? extends Series> seriesSet;
    private final int maxDownloads;

    private final Set<Download> currentDownloads;
    private final Queue<Series> seriesQueue;
    private final Queue<Downloader> downloadGenerateQueue;
    private final Queue<Downloader> downloadAccessQueue;

    private final AtomicInteger finishedDownloads;
    private State state;

    private long slowModeDelay;

    CollectiveDownload(Set<? extends Series> seriesSet, int maxDownloads) {
        this.maxDownloads = maxDownloads;
        this.seriesSet = seriesSet;
        this.currentDownloads = ConcurrentHashMap.newKeySet(maxDownloads);
        seriesQueue = new LinkedBlockingQueue<>();
        downloadGenerateQueue = new LinkedBlockingQueue<>();
        downloadAccessQueue = new LinkedBlockingQueue<>();
        finishedDownloads = new AtomicInteger();
        slowModeDelay = 0;
        state = State.IDLE;

        seriesQueue.addAll(seriesSet);
    }

    //TODO: Implement download() with JavaDoc
    public void download() {
        if (state == State.IDLE || state == State.PAUSED) {
            state = State.RUNNING;

            Thread downloadGenerateThread = new Thread(() -> {
                while (!seriesQueue.isEmpty() && !Thread.interrupted()) {
                    Series series = seriesQueue.poll();

                    if (series.isEmpty()) {
                        try {
                            series.fillDownloaders();
                            //noinspection BusyWait
                            Thread.sleep(slowModeDelay);
                        } catch (MalformedURLException e) {
                            System.out.println(e.getMessage());
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }

                    for (Downloader downloader : series) {
                        downloadGenerateQueue.add(downloader);
                    }
                }
            });

            Thread downloadAccessThread = new Thread(() -> {

            });

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
