package org.jacob.im.obfo.monitor;

import org.jacob.im.obfo.constants.OBFOConstants;
import org.jacob.im.obfo.monitor.core.NewFilesAddedWatcher;

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
                .get(OBFOConstants.UNCLASSIFIED_REMAINING_IMAGES_FOLDER_PATH));
        return addFileMonitor.toString();
    }
}