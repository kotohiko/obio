package org.jacob.im.obfo.service;

import org.jacob.im.common.constants.IMCommonConstants;
import org.jacob.im.common.response.ResManager;
import org.jacob.im.obfo.constants.OBFOConstants;
import org.jacob.im.obfo.controller.ReadAndMoveController;
import org.jacob.im.obfo.enums.FilesMoveOperStatusEnums;
import org.jacob.im.obfo.logger.OBFOLogFilesWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.Objects;

/**
 * Main class that reading and moving files.
 *
 * @author Kotohiko
 * @since 07:26 Aug 02, 2024
 */
public class ReadAndMoveService {

    /**
     * The logger instance used for logging messages related to the {@link ReadAndMoveService} class.
     * This logger is configured to log messages at various levels (e.g., debug, info, error) and can be
     * used throughout the class to provide detailed information about the watcher's operations.
     */
    private static final Logger logger = LoggerFactory.getLogger(ReadAndMoveService.class);

    /**
     * Loads a {@link FileInputStream} for the YAML configuration file.
     * If the file is not found, logs an error message and performs a system reboot.
     *
     * @return FileInputStream representing the YAML file stream, or null if the file is not found.
     */
    public static FileInputStream loadYamlFile() {
        FileInputStream ymlFileStream = null;
        try {
            ymlFileStream = new FileInputStream(OBFOConstants.ILLUSTRATIONS_CONF_YML_PATH);
        } catch (FileNotFoundException e) {
            logger.error(ResManager.loadResString("ReadAndMoveService_1"));
            endLinePrintAndReboot();
        }
        return ymlFileStream;
    }

    /**
     * Moves files from a default source path to a target path specified by a target path code.
     *
     * <p>This method first defines the source path using the provided default source path. It then
     * retrieves the target path string from the provided map of paths data using the target path code.
     * If the target path string is null, an error message is logged. Otherwise, it calls the
     * {@link #checkBeforeMove(Path, String)} method to perform any necessary checks before moving
     * the files.
     *
     * @param defaultSourcePath The default source path from which files will be moved.
     * @param pathsData         A map containing path data where the key is the target path code and the value
     *                          is the corresponding target path string.
     * @param targetPathCode    The target path code used to retrieve the target path string from the
     *                          paths data map.
     */
    public static void filesMove(String defaultSourcePath,
                                 Map<String, String> pathsData, String targetPathCode) {
        // Define source path and target path
        Path sourcePath = Paths.get(defaultSourcePath);
        String targetPathStr = pathsData.get(targetPathCode);
        if (targetPathStr == null) {
            logger.error(ResManager.loadResString("ReadAndMoveService_0"));
        } else {
            checkBeforeMove(sourcePath, targetPathStr);
        }
        System.out.println(IMCommonConstants.SEPARATOR_LINE);
    }

    /**
     * Necessary validation before and after moving files.
     *
     * @param sourcePath    The source path
     * @param targetPathStr The target path string
     */
    private static void checkBeforeMove(Path sourcePath, String targetPathStr) {
        Path targetPath = Paths.get(targetPathStr);
        FilesMoveOperStatusEnums statusEnums = FilesMoveOperStatusEnums.NO_FILES;
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(sourcePath)) {
            for (Path filePath : directoryStream) {
                // Ignore directories and process only files.
                if (Files.isRegularFile(filePath)) {
                    statusEnums = moveTheFiles(targetPath, filePath);
                }
            }
            // Check whether the file is found after traversal is completed.
            if (statusEnums.equals(FilesMoveOperStatusEnums.NO_FILES)) {
                logger.error(ResManager.loadResString("ReadAndMoveService_3"), sourcePath);
            }
        } catch (IOException e) {
            logger.error(ResManager.loadResString("ReadAndMoveService_2"));
        }
    }

    /**
     * Moves a file from its current location to a specified target directory.
     * <p>
     * This method constructs the target file path by resolving the given file name with the target directory path.
     * It then attempts to move the file to the new location, replacing any existing file with the same name
     * in the target directory.
     * <p>
     * If the move operation is successful, the method logs an info message and increments the count of
     * files moved (assuming {@link ReadAndMoveService#countTheNumberOfFiles()} updates a count).
     * In case of an error, such as if the target path is invalid or the file cannot be moved for any reason,
     * it logs an error message and returns an error status.
     *
     * @param targetPath The target directory where the file should be moved to.
     * @param filePath   The path of the file to be moved.
     * @return A {@link FilesMoveOperStatusEnums} enum indicating the status of the operation.
     * - {@link FilesMoveOperStatusEnums#HAS_FILES} if the file was successfully moved.
     * - {@link FilesMoveOperStatusEnums#TARGET_PATH_INVALID} if the target path is invalid or the move failed
     * for any other reason.
     * @see Files#move(Path, Path, CopyOption...)
     * @see StandardCopyOption#REPLACE_EXISTING
     */
    private static FilesMoveOperStatusEnums moveTheFiles(Path targetPath, Path filePath) {
        // Construct the target path
        Path targetFilePath = targetPath.resolve(filePath.getFileName());
        try {
            Files.move(filePath, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info(ResManager.loadResString("ReadAndMoveService_4"), filePath, targetFilePath);
            countTheNumberOfFiles();
            // Signal that the files have been found
            return FilesMoveOperStatusEnums.HAS_FILES;
        } catch (IOException e) {
            logger.error(ResManager.loadResString("ReadAndMoveService_5"));
            return FilesMoveOperStatusEnums.TARGET_PATH_INVALID;
        }
    }

    /**
     * Counts the number of files in the specified directory and writes the count to a log file.
     * This method traverses all the files under the directory specified by
     * {@code OBFOConstants.UNCLASSIFIED_REMAINING_IMAGES_FOLDER_PATH}, counts the number of files,
     * and then calls {@code OBFOLogFilesWriter.filesMoveLogWriter} to write the count to a log file.
     */
    private static void countTheNumberOfFiles() {
        File folder = new File(OBFOConstants.UNCLASSIFIED_REMAINING_IMAGES_FOLDER_PATH);
        int fileCount = 0;
        // Traverse all the files under the specified directory.
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isFile()) {
                ++fileCount;
            }
        }
        OBFOLogFilesWriter.filesMoveLogWriter(fileCount);
    }

    public static void endLinePrintAndReboot() {
        System.out.println(IMCommonConstants.SEPARATOR_LINE);
        ReadAndMoveController.mainPart();
    }
}