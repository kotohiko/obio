package org.jacob.im.obfo.service;

import org.jacob.im.common.IMCommonConsoleInputReader;
import org.jacob.im.common.constants.IMCommonConstants;
import org.jacob.im.obfo.constants.OBFOConstants;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

/**
 * Main class that reading and moving files.
 *
 * @author Jacob Suen
 * @since 07:26 Aug 02, 2024
 */
public class ReadAndMoveService {

    /**
     * Main Execution Method
     */
    public static void serviceMainPart() {
        System.out.println(OBFOConstants.WELCOME_LINE);
        try (BufferedReader in = IMCommonConsoleInputReader.consoleReader()) {
            String targetPathKey;
            Yaml yaml = new Yaml();
            while (true) {
                System.out.print("Please enter your target path code: ");
                if ((targetPathKey = in.readLine()) == null) {
                    break;
                }
                // Load a Yaml file into a Java object.
                Map<String, String> pathsData = yaml.load(loadYamlFile());
                String defaultSourcePath = pathsData.get("Default source path");
                if (defaultSourcePath == null || defaultSourcePath.isEmpty()) {
                    System.out.println("Failed to get the source path. Please check if the source path mapping "
                            + "in the YAML file matches the actual local path.");
                } else {
                    filesMoving(defaultSourcePath, pathsData, targetPathKey);
                }
            }
        } catch (IOException e) {
            System.out.println("Cannot read your input contents, please check and try again.");
        }
        endLinePrintAndReboot();
    }

    /**
     * Loading the yml file.
     *
     * @return Yaml file stream
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
    private static void filesMoving(String defaultSourcePath,
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
        boolean hasFiles = false;
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(sourcePath)) {
            for (Path filePath : directoryStream) {
                // Ignore directories and process only files.
                if (Files.isRegularFile(filePath)) {
                    hasFiles = moveTheFiles(targetPath, filePath);
                }
            }
            // 遍历完成后检查是否找到文件
            if (!hasFiles) {
                System.out.println("No files found in the source directory: " + sourcePath);
            }
        } catch (IOException e) {
            System.out.println("An IOException occurred while creating source path stream, "
                    + "please identify and rectify the source of the problem.");
        }
    }

    /**
     * Run the file moving logic
     *
     * @param targetPath The target path
     * @param filePath   The path of file
     * @return whether the process for moving files was executed correctly.
     * {@code true} for yes and {@code false} for no.
     */
    private static boolean moveTheFiles(Path targetPath, Path filePath) {
        // 构建目标路径
        Path targetFilePath = targetPath.resolve(filePath.getFileName());
        // 移动文件
        try {
            Files.move(filePath, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File has been moved: " + filePath + " -> " + targetFilePath);
            logWriter();
            // Signal that the files have been found
            return true;
        } catch (IOException e) {
            System.out.println("Failed to move files. Please verify that the target path is valid.");
            return false;
        }
    }

    /**
     * Log writer
     */
    private static void logWriter() {
        File folder = new File(OBFOConstants.UNCLASSIFIED_REMAINING_IMAGES_FOLDER_PATH);
        int fileCount = 0;
        // 遍历文件夹中的所有文件
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isFile()) {
                ++fileCount;
            }
        }
        // Record the file count and date in the log file
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(OBFOConstants.UNCLASSIFIED_REMAINING_IMAGES_LOG_PATH, true))) {
            String date = LocalDate.now().toString();
            bw.write(date + " INFO [Client] - File(s) has/have been moved; "
                    + "Remaining unclassified images: " + fileCount + "\n");
        } catch (IOException e) {
            System.out.println("The log file cannot be found. Please check if the file name "
                    + "or path is configured correctly.");
        }
    }

    private static void endLinePrintAndReboot() {
        System.out.println(IMCommonConstants.SEPARATOR_LINE);
        serviceMainPart();
    }
}