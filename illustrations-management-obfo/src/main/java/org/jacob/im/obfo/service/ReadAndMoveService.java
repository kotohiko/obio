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

    private static final Logger logger = LoggerFactory.getLogger(ReadAndMoveService.class);

    /**
     * Load the YAML file.
     *
     * @return YAML file stream
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
     * Moving files service.
     *
     * @param defaultSourcePath The default source path
     * @param pathsData         Path mapping data
     * @param targetPathCode    Code of the target path
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
     * Move the files.
     *
     * @param targetPath The target path
     * @param filePath   The path of file
     * @return whether the process for moving files was executed correctly.
     * {@code true} for yes and {@code false} for no.
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
     * Count the number of files, and write the result into the log.
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