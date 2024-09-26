package org.jacob.im.obfo.monitor.core;

import org.jacob.im.common.response.ResManager;
import org.jacob.im.obfo.constants.OBFOConstants;
import org.jacob.im.obfo.logger.OBFOLogFilesWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A monitor that watches for changes and events related to files.
 *
 * @author Kotohiko
 * @since 16:29 Aug 18, 2024
 */
public class NewFilesAddedWatcher {

    /**
     * Used to identify the thread, and is also the name of the class that created this thread.
     */
    private final String WATCHER_THREAD_NAME = "NewFilesAddedWatcher-Thread";

    /**
     * To avoid thread safety issues, atomic classes are used here.
     */
    private final AtomicInteger THREAD_ID_SEQ = new AtomicInteger(0);

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
     * An {@link Executor} that provides methods to manage termination and
     * methods that can produce a {@link Future} for tracking progress of
     * one or more asynchronous tasks.
     */
    private final ExecutorService executor;

    /**
     * Constructor of {@link NewFilesAddedWatcher}.<p>
     * This will submit the{@link NewFilesAddedWatcher#startWatching()}method of the current object
     * as a task to the executor thread pool for asynchronous execution. This task will be executed
     * on a thread in the thread pool without blocking the current thread.<p>
     * If you don't understand the new writing style:{@code executor.submit(this::startWatching);},
     * you can refer to the old writing style:{@code executor.submit(() -> startWatching());}
     *
     * @param dir             The directory that needs to be monitored
     * @param numberOfThreads Number of threads
     */
    public NewFilesAddedWatcher(Path dir, int numberOfThreads) {
        try {
            this.watcher = FileSystems.getDefault()
                    // Constructs a new WatchService optional operation.
                    .newWatchService();
            this.dir = dir;
            // Create a fixed-size thread pool
            this.executor = Executors.newFixedThreadPool(numberOfThreads, r -> {
                Thread t = new Thread(r, WATCHER_THREAD_NAME + "-" + THREAD_ID_SEQ.incrementAndGet());
                // Set the current thread as a daemon thread
                t.setDaemon(true);
                return t;
            });

            // Start monitoring the directory upon initialization
            dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
            executor.submit(this::startWatching);
            printMemoryInfo();
            printThreadsInfo();
        } catch (IOException e) {
            logger.error(ResManager.loadResString("NewFilesAddedWatcher_1"));
            throw new RuntimeException(e);
        }
    }

    /**
     * Starts watching the directory for new file events. This method runs an infinite loop,
     * blocking and waiting for events to occur. When an event is detected, it submits a task
     * to the thread pool to handle the event. The task processes each event by resolving the
     * file path and invoking the{@code processFile}method.
     *
     * <p>If an{@code InterruptedException}is caught, it indicates that the thread has been
     * interrupted, typically as a result of a request to stop watching. In this case, an error
     * message is logged, and the thread pool is gracefully shut down.
     */
    private void startWatching() {
        try {
            while (true) {
                // Block and wait for event occurs
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
                        // Use a typed cast to eliminate unchecked cast warning
                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
                        Path filename = dir.resolve(ev.context());
                        // Validate the file path before processing
                        Files.exists(filename);
                        processFile(filename);
                    }
                });

                // Reset the key
                var valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            logger.error(ResManager.loadResString("NewFilesAddedWatcher_0"));
            // May need to gracefully shut down the thread pool.
            shutdown();
        }
    }

    /**
     * Record the total of files.
     *
     * @param fileWithAbsPath Files with absolute path
     */
    private void processFile(Path fileWithAbsPath) {
        var folder = new File(OBFOConstants.UNCLASSIFIED_REMAINING_IMAGES_FOLDER_PATH);

        // Check if the folder exists and is a directory
        if (!folder.exists() || !folder.isDirectory()) {
            logger.error(ResManager.loadResString("NewFilesAddedWatcher_3",
                    OBFOConstants.UNCLASSIFIED_REMAINING_IMAGES_FOLDER_PATH));
            return;
        }

        var fileCount = 0;
        try (var stream = Files.list(folder.toPath())) {
            fileCount = (int) stream.filter(Files::isRegularFile).count();
        } catch (IOException e) {
            logger.error(ResManager.loadResString("NewFilesAddedWatcher_4",
                    OBFOConstants.UNCLASSIFIED_REMAINING_IMAGES_FOLDER_PATH));
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