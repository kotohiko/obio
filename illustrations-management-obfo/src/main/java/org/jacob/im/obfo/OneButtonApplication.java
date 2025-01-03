package org.jacob.im.obfo;

import org.jacob.im.obfo.controller.ReadAndMoveController;
import org.jacob.im.obfo.monitor.MonitorStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a one-button application that initializes and monitors file additions.
 * This class sets up a monitor to track added files and logs relevant information.
 * It uses a logger to record operational details and coordinates the reading and moving of files.
 *
 * @author Kotohiko
 * @since 07:26 Aug 02, 2024
 */
public final class OneButtonApplication {

    /**
     * The logger instance used for logging messages related to the {@link OneButtonApplication} class.<p>
     * This logger is configured to log messages at various levels (e.g., debug, info, error) and can be
     * used throughout the class to provide detailed information about the watcher's operations.
     */
    private static final Logger logger = LoggerFactory.getLogger(OneButtonApplication.class);

    /**
     * The entry point of the application.
     * <p>
     * This method starts the monitor, logs the successful start,
     * and invokes the main functionality of the read and move controller.
     *
     * @param args Command line arguments, not used in this context.
     */
    public static void main(String[] args) {
        var addFileMonitor = MonitorStarter.monitorStarter();
        logger.info("{} has started successfully.", addFileMonitor);
        new ReadAndMoveController().mainPart();
    }
}