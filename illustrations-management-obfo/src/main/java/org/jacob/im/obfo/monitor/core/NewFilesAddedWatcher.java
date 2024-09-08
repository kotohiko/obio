package org.jacob.im.obfo.monitor.core;

import org.jacob.im.obfo.constants.OBFOConstants;
import org.jacob.im.obfo.log.OBFOLogWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * A monitor that watches for changes and events related to files.
 *
 * @author Jacob Suen
 * @since 16:29 Aug 18, 2024
 */
public class NewFilesAddedWatcher {

    /**
     * A watch service that <em>watches</em> registered objects for changes and events.
     */
    private final WatchService watcher;

    /**
     * An object that may be used to locate a file in a file system.
     * It will typically represent a system dependent file path.
     */
    private final Path dir;

    /**
     * An {@link Executor} that provides methods to manage termination and
     * methods that can produce a {@link Future} for tracking progress of
     * one or more asynchronous tasks.
     */
    private final ExecutorService executor;

    /**
     * Constructor of {@link NewFilesAddedWatcher}.
     *
     * @param dir             The directory that needs to be monitored
     * @param numberOfThreads Number of threads
     */
    public NewFilesAddedWatcher(Path dir, int numberOfThreads) {
        try {
            this.watcher = FileSystems.getDefault().newWatchService();
            this.dir = dir;
            // Create a fixed-size thread pool
            this.executor = Executors.newFixedThreadPool(numberOfThreads);
            // Start monitoring the directory upon initialization
            dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
            // Submit a task to start monitoring
            executor.submit(this::startWatching);
        } catch (IOException e) {
            System.out.println("An IOException has occurred. Please identify and rectify the source of the problem.");
            throw new RuntimeException(e);
        }
    }

    /**
     *
     */
    private void startWatching() {
        try {
            while (true) {
                // 阻塞等待事件发生
                var key = watcher.take();
                // Submit a task for each event in the thread pool.
                executor.submit(() -> {
                    var events = key.pollEvents();
                    for (WatchEvent<?> event : events) {
                        WatchEvent.Kind<?> kind = event.kind();
                        if (kind == StandardWatchEventKinds.OVERFLOW) {
                            continue;
                        }

                        @SuppressWarnings("unchecked")
                        var ev = (WatchEvent<Path>) event;
                        var filename = dir.resolve(ev.context());
                        processFile(filename);
                    }
                });
                // 重置key
                var valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            System.out.println("An InterruptedException occurred."
                    + " Please identify and rectify the source of the problem.");
            // May need to gracefully shut down the thread pool.
            shutdown();
        }
    }

    /**
     * Record the total of files
     *
     * @param fileWithAbsPath Files with absolute path
     */
    private void processFile(Path fileWithAbsPath) {
        var folder = new File(OBFOConstants.UNCLASSIFIED_REMAINING_IMAGES_FOLDER_PATH);
        var fileCount = 0;
        // Iterate through all files in the directory
        for (File allFiles : Objects.requireNonNull(folder.listFiles())) {
            if (allFiles.isFile()) {
                ++fileCount;
            }
        }
        OBFOLogWriter.filesAddedLogWriter(fileWithAbsPath, fileCount);
    }

    /**
     * Shut down the thread pool gracefully.
     */
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}