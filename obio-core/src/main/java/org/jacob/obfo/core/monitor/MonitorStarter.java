package org.jacob.obfo.core.monitor;

import org.jacob.obfo.core.constants.ObioConstants;
import org.jacob.obfo.core.monitor.core.NewFilesAddedWatcher;

import java.nio.file.Paths;

/**
 * This class provides a static method to start monitoring a specified directory for new files.
 * It initializes an instance of{@link NewFilesAddedWatcher}with the given directory path and
 * a specified interval for checking new files.
 *
 * @author Kotohiko
 * @since 10:16 Sep 03, 2024
 */
public class MonitorStarter {
    /**
     * Starts monitoring the specified directory for new files and returns a string representation
     * of the initialized{@link NewFilesAddedWatcher}.
     *
     * @return A string representation of the initialized{@link NewFilesAddedWatcher}.
     */
    public static String monitorStarter() {
        var addFileMonitor = new NewFilesAddedWatcher(Paths
                .get(ObioConstants.PATH_OF_UNCLASSIFIED_REMAINING_IMAGES));
        return addFileMonitor.toString();
    }
}