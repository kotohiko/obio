package org.jacob.im.common.utils.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link CustomThreadFactory} is an implementation of the {@link ThreadFactory} interface
 * that allows for custom naming of threads created in a thread pool.
 * Each thread name is prefixed with a specified base name followed by a unique, incrementing number.
 * <p>
 * This is useful for debugging and monitoring purposes, making it easier to identify and track
 * threads in a multi-threaded environment.
 * <p>
 * Example thread names: ReadAndMovePool-thread-1, ReadAndMovePool-thread-2, etc.
 */
public class CustomThreadFactory implements ThreadFactory {

    /**
     * Base name for all threads in the pool
     */
    private final String baseName;

    /**
     * Counter to give unique IDs to threads
     */
    private final AtomicInteger threadCount = new AtomicInteger(1);

    /**
     * Constructs a CustomThreadFactory with a specified base name for threads.
     *
     * @param baseName the base name for threads created by this factory
     */
    public CustomThreadFactory(String baseName) {
        this.baseName = baseName;
    }

    /**
     * Creates a new thread with a custom name consisting of the base name followed by a unique ID.
     *
     * @param r the runnable task to be executed by the new thread
     * @return a new {@link Thread} object with a custom name
     */
    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        // Set custom thread name with incrementing thread count
        thread.setName(baseName + "-thread-" + threadCount.getAndIncrement());
        return thread;
    }
}
