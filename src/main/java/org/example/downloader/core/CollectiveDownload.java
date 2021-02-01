package org.example.downloader.core;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

//TODO: Implement with good quality (right now only to get alpha release)
public class CollectiveDownload {
    private final Set<? extends Downloader> downloaderSet;

    public CollectiveDownload(Set<? extends Downloader> downloaderSet) {
        this.downloaderSet = downloaderSet;
    }

    public void execute(String path, String format, int maxDownloads) {
        WebScrapers.javaScript().quit();
        LinkedHashSet<Download> current = new LinkedHashSet<>();
        Queue<Downloader> queued = new LinkedList<>();
        final Map<Download, String> formatMap = new HashMap<>();
        AtomicInteger finished = new AtomicInteger();

        queued.addAll(downloaderSet);
        /*Thread downloadGenerator = new Thread(() -> {
            for (Downloader downloader : downloaderSet) {
                Download download = null;
                String formatted = null;
                boolean empty = false;
                do{
                    try {
                        synchronized (current){
                            empty = current.size() < maxDownloads;
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }while (!empty);
                try {
                    EpisodeFormat episodeFormat = downloader.generateEpisodeFormat();
                    formatted = episodeFormat.format(format);
                    download = downloader.generateVideoDownload(path + File.separator + episodeFormat.format("/S"), format, (bytes) -> {
                    });
                } catch (MalformedURLException | NullPointerException | IndexOutOfBoundsException e) {
                    //System.out.print(downloader.pageURL + ": ");
                    //e.printStackTrace();
                }
                if (download != null) {
                    synchronized (queued) {
                        queued.add(download);
                        formatMap.put(download, formatted);
                    }
                    synchronized (prepared) {
                        prepared.getAndIncrement();
                    }
                }
            }
        });
        downloadGenerator.start();*/

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
