package org.example.downloader.core;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

//TODO: Implement with good quality (right now only to get alpha release)
public class CollectiveDownload {
    private final Set<? extends Downloader> downloaderSet;

    public CollectiveDownload(Set<? extends Downloader> downloaderSet) {
        this.downloaderSet = downloaderSet;
    }

    public void execute(String path, String format, int maxDownloads) {
        LinkedHashSet<Download> current = new LinkedHashSet<>();
        Queue<Download> queued = new LinkedList<>();
        final Map<Download, String> formatMap = new HashMap<>();

        for (Downloader downloader : downloaderSet) {
            Download download = null;
            String formatted = null;
            try {
                EpisodeFormat episodeFormat = downloader.generateEpisodeFormat();
                formatted = episodeFormat.format(format);
                download = downloader.generateVideoDownload(path + File.separator + episodeFormat.format("/S"), format, (bytes) -> {
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (download != null) {
                queued.add(download);
                formatMap.put(download, formatted);
            }
        }

        long time = System.currentTimeMillis();
        while (!(queued.isEmpty() && current.isEmpty())) {
            synchronized (current) {
                if (current.size() < maxDownloads) {
                    Download download = queued.poll();
                    if (!download.isAlreadyDownloaded()) {
                        current.add(download);
                        download.startParallel((success) -> {
                            synchronized (System.out) {
                                System.out.println("\rDownload: " + formatMap.get(download) + " is finished!");
                            }
                            synchronized (current) {
                                current.remove(download);
                            }
                        });
                        try {
                            download.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }

            synchronized (current) {
                Download[] downloads = current.toArray(Download[]::new);
                synchronized (System.out) {
                    System.out.print("\r");
                    for (int i = 0; i < downloads.length; i++) {
                        if (i > 0) {
                            System.out.print(" | ");
                        }
                        System.out.print("Download: " + formatMap.get(downloads[i]) + " " + Math.round(((double) downloads[i].getDownloaded() / downloads[i].getSize()) * 100D) + "%");
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
