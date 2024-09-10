package org.jacob.im.obfo.service;

import org.jacob.im.common.constants.IMCommonConstants;
import org.jacob.im.common.helper.IMCommonHelper;
import org.jacob.im.obfo.constants.OBFOConstants;
import org.jacob.im.obfo.enums.FilesMoveOperStatusEnums;
import org.jacob.im.obfo.logger.OBFOLogger;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
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
     * Main Execution Method
     */
    public static void serviceMainPart() {
        System.out.print(OBFOConstants.WELCOME_LINE);
        try (BufferedReader in = IMCommonHelper.consoleReader()) {
            String targetPathKey;
            Yaml yaml = new Yaml();
            while (true) {
                System.out.print("Please enter your target path code: ");
                if ((targetPathKey = in.readLine()) == null) {
                    break;
                }
                // Load a YAML file into a Java object.
                Map<String, String> pathsData = yaml.load(loadYamlFile());
                String defaultSourcePath = pathsData.get("Default source path");
                if (defaultSourcePath == null || defaultSourcePath.isEmpty()) {
                    System.out.println("Failed to get the source path. Please check if the source path mapping "
                            + "in the YAML file matches the actual local path.");
                } else {
                    filesMove(defaultSourcePath, pathsData, targetPathKey);
                }
            }
        } catch (IOException e) {
            System.out.println("Cannot read your input contents, please check and try again.");
        }
        endLinePrintAndReboot();
    }

    /**
     * Load the YAML file.
     *
     * @return YAML file stream
     */
    private static FileInputStream loadYamlFile() {
        FileInputStream ymlFileStream = null;
        try {
            ymlFileStream = new FileInputStream(OBFOConstants.ILLUSTRATIONS_CONF_YML_PATH);
        } catch (FileNotFoundException e) {
            System.out.println("The Yaml file does not exist. Please check if the arguments"
                    + " passed to the FileInputStream object constructor match the actual path.");
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
    private static void filesMove(String defaultSourcePath,
                                  Map<String, String> pathsData, String targetPathCode) {
        // Define source path and target path
        Path sourcePath = Paths.get(defaultSourcePath);
        String targetPathStr = pathsData.get(targetPathCode);
        if (targetPathStr == null) {
            System.out.println("The entered path code does not match any valid path. Please check and correct it.");
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
                System.out.println("No files found in the source directory: " + sourcePath);
            }
        } catch (IOException e) {
            System.out.println("An IOException occurred while creating source path stream. "
                    + "The source path might not exist, or there could be other problems");
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
            System.out.println("File has been moved: " + filePath + " -> " + targetFilePath);
            countTheNumberOfFiles();
            // Signal that the files have been found
            return FilesMoveOperStatusEnums.HAS_FILES;
        } catch (IOException e) {
            System.out.println("Failed to move files. Please verify that the target path is valid.");
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
        OBFOLogger.filesMoveLogWriter(fileCount);
    }

    private static void endLinePrintAndReboot() {
        System.out.println(IMCommonConstants.SEPARATOR_LINE);
        serviceMainPart();
    }
}