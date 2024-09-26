package org.jacob.im.obfo.logger;

import org.jacob.im.common.helper.IMCommonHelper;
import org.jacob.im.common.response.ResManager;
import org.jacob.im.obfo.constants.OBFOConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

/**
 * This class is responsible for writing log messages to a file.
 *
 * @author Kotohiko
 * @since 17:14 Sep 08, 2024
 */
public class OBFOLogFilesWriter {

    /**
     * The logger instance used for logging messages related to the{@link OBFOLogFilesWriter}class.
     * This logger is configured to log messages at various levels (e.g., debug, info, error) and can be
     * used throughout the class to provide detailed information about the watcher's operations.
     */
    private static final Logger logger = LoggerFactory.getLogger(OBFOLogFilesWriter.class);

    /**
     * Writes a log entry to the specified log file indicating the number of new files added and the
     * remaining unclassified images.
     *
     * @param fileWithAbsPath The path of the newly added file with absolute path.
     * @param fileCount       The number of remaining unclassified images.
     */
    public static void filesAddedLogWriter(Path fileWithAbsPath, int fileCount) {
        // Write the number of files and date to log file.
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(OBFOConstants.PATH_OF_UNCLASSIFIED_REMAINING_IMAGES_LOG, Boolean.TRUE))) {
            bw.write(IMCommonHelper.getRealTime()
                    + " [main] INFO - New files added: " + fileWithAbsPath.getFileName()
                    + "; Remaining unclassified images: " + fileCount + "\n");
        } catch (IOException e) {
            logger.error(ResManager.loadResString("OBFOLogger_0"));
        }
    }

    /**
     * Writes a log entry to the specified log file indicating that files have been moved and the
     * remaining unclassified images.
     *
     * @param fileCount The number of remaining unclassified images.
     */
    public static void filesMoveLogWriter(int fileCount) {
        // Record the file count and date in the log file.
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(OBFOConstants.PATH_OF_UNCLASSIFIED_REMAINING_IMAGES_LOG, Boolean.TRUE))) {
            bw.write(IMCommonHelper.getRealTime()
                    + " [main] INFO - New files added: - File(s) has/have been moved; "
                    + "Remaining unclassified images: " + fileCount + "\n");
        } catch (IOException e) {
            logger.error(ResManager.loadResString("OBFOLogger_0"));
        }
    }
}