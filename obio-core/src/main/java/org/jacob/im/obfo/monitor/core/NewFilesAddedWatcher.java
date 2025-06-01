package org.jacob.im.obfo.monitor.core;

import org.jacob.im.common.response.ResManager;
import org.jacob.im.obfo.constants.ObioConstants;
import org.jacob.im.obfo.logger.OBFOLogFilesWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.nio.file.*;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * A monitor that watches for changes and events related to files.
 *
 * @author Kotohiko
 * @since 16:29 Aug 18, 2024
 */
public class NewFilesAddedWatcher {

    /**
     * The logger instance used for logging messages related to the {@link NewFilesAddedWatcher} class.
     * This logger is configured to log messages at various levels (e.g., debug, info, error) and can be
     * used throughout the class to provide detailed information about the watcher's operations.
     */
    private final Logger logger = LoggerFactory.getLogger(NewFilesAddedWatcher.class);

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
     * Constructs a new {@link NewFilesAddedWatcher} instance that monitors a specified directory
     * for newly created files. This class uses a {@link WatchService} to track file creation events
     * and a single-threaded {@link ThreadPoolExecutor} to handle the event processing.
     *
     * @param dir the directory {@link Path} to be monitored for newly added files
     * @throws RuntimeException if an {@link IOException} occurs during the initialization of the WatchService
     *
     *                          <p>
     *                          The constructor performs the following actions:
     *                          <ul>
     *                              <li>Initializes a WatchService to monitor the specified directory for {@link StandardWatchEventKinds#ENTRY_CREATE} events.</li>
     *                              <li>Creates a single-threaded {@link ThreadPoolExecutor} with a bounded queue to handle file system events.</li>
     *                              <li>Submits a task to the executor service to start watching the directory for file creation events.</li>
     *                              <li>Prints memory and thread information for debugging and monitoring purposes.</li>
     *                          </ul>
     *                          If an error occurs while initializing the WatchService, a {@code RuntimeException} is thrown.
     */
    public NewFilesAddedWatcher(Path dir) {
        try {
            this.watcher = FileSystems.getDefault().newWatchService();
            this.dir = dir;

            // Register the directory for the ENTRY_CREATE event
            dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);

            // Start a dedicated thread to monitor the directory
            Thread watcherThread = new Thread(this::startWatching);
            // Set as a daemon thread to terminate with the program
            watcherThread.setDaemon(true);
            watcherThread.start();

            printMemoryInfo();
            printThreadsInfo();
        } catch (IOException e) {
            logger.error(ResManager.loadResString("NewFilesAddedWatcher_1"));
            throw new RuntimeException(e);
        }
    }

    /**
     * Starts watching the directory for new file events. This method runs an infinite loop,
     * blocking and waiting for events to occur. When an event is detected, it processes each event
     * directly in the current thread by resolving the file path and invoking the {@code processFile} method.
     * <p>
     * If an {@link InterruptedException} is caught, it indicates that the thread has been
     * interrupted, typically as a result of a request to stop watching. In this case, an error
     * message is logged, and the thread pool is gracefully shut down.
     */
    private void startWatching() {
        try {
            while (true) {
                // Block and wait for event occurs
                var key = watcher.take();

                // Process events directly in the current thread
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = dir.resolve(ev.context());

                    // Validate the file path before processing
                    if (Files.exists(filename)) {
                        processFile(filename);
                    }
                }

                // Reset the key
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            logger.error(ResManager.loadResString("NewFilesAddedWatcher_0"));
        }
    }

    /**
     * Record the total of files.
     *
     * @param fileWithAbsPath Files with absolute path
     */
    private void processFile(Path fileWithAbsPath) {
        var folder = new File(ObioConstants.PATH_OF_UNCLASSIFIED_REMAINING_IMAGES);
        int fileCount;

        // Check if the folder exists and is a directory
        if (!folder.exists() || !folder.isDirectory()) {
            logger.error(ResManager.loadResString("NewFilesAddedWatcher_3",
                    ObioConstants.PATH_OF_UNCLASSIFIED_REMAINING_IMAGES));
            return;
        }

        try (var stream = Files.list(folder.toPath())) {
            fileCount = (int) stream.filter(Files::isRegularFile).count();
        } catch (IOException e) {
            logger.error(ResManager.loadResString("NewFilesAddedWatcher_4",
                    ObioConstants.PATH_OF_UNCLASSIFIED_REMAINING_IMAGES));
            return;
        }

        OBFOLogFilesWriter.filesAddedLogWriter(fileWithAbsPath, fileCount);
    }

    /**
     * Prints information about the memory usage of the Java Virtual Machine (JVM). This method
     * retrieves the total amount of memory, the amount of free memory, and calculates the used
     * memory by subtracting the free memory from the total memory. The memory usage is then logged
     * in megabytes (MB).
     */
    private void printMemoryInfo() {
        // Print memory usage
        var runtime = Runtime.getRuntime();
        // The total amount of memory in the Java virtual machine
        var totalMemory = runtime.totalMemory();
        // The amount of free memory in the Java Virtual Machine
        var freeMemory = runtime.freeMemory();
        var usedMemory = totalMemory - freeMemory;

        logger.info("Total Heap memory: {} MB", totalMemory / 1024 / 1024);
        logger.info("Heap free memory: {} MB", freeMemory / 1024 / 1024);
        logger.info("Heap used memory: {} MB", usedMemory / 1024 / 1024);
    }

    /**
     * Prints information about all threads in the Java Virtual Machine (JVM).
     * This method retrieves thread information using the {@link ThreadMXBean} and logs the
     * name and state of each thread.
     */
    private void printThreadsInfo() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        var threadIds = threadMXBean.getAllThreadIds();

        for (long id : threadIds) {
            ThreadInfo threadInfo = threadMXBean.getThreadInfo(id);
            logger.info("Thread Name: {}, State: {}", threadInfo.getThreadName(), threadInfo.getThreadState());
        }
    }
}