package org.jacob.im.obfo.monitor;

import org.jacob.im.obfo.constants.OBFOConstants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Add file monitor.
 *
 * @author Jacob Suen
 * @since 16:29 Aug 18, 2024
 */
public class AddingFileOperMonitor {

    private final WatchService watcher;
    private final Path dir;
    private final ExecutorService executor;

    public AddingFileOperMonitor(Path dir, int numberOfThreads) {
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
            throw new RuntimeException(e);
        }
    }

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
                        // 处理文件
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
            System.out.println("遇到了中断异常");
            // 可能需要优雅地关闭线程池
            shutdown();
        }
    }

    private void processFile(Path fileWithAbsPath) {
        var folder = new File(OBFOConstants.UNCLASSIFIED_REMAINING_IMAGES_FOLDER_PATH);
        var fileCount = 0;
        // Iterate through all files in the directory
        for (File allFiles : Objects.requireNonNull(folder.listFiles())) {
            if (allFiles.isFile()) {
                ++fileCount;
            }
        }
        // Write the number of files and date to log file.
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(OBFOConstants.UNCLASSIFIED_REMAINING_IMAGES_LOG_PATH, true))) {
            var date = LocalDate.now().toString();
            bw.write(date + " INFO [Client] - " + "New files added: " + fileWithAbsPath.getFileName()
                    + "; Remaining unclassified images: " + fileCount + "\n");
        } catch (IOException e) {
            System.out.println("The log file cannot be found. Please check if the file name "
                    + "or path is configured correctly.");
        }
    }

    // 优雅地关闭线程池
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