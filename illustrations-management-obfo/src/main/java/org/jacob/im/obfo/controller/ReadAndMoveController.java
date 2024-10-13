package org.jacob.im.obfo.controller;

import org.jacob.im.common.constants.IMCommonConstants;
import org.jacob.im.common.helper.IMCommonHelper;
import org.jacob.im.common.response.ResManager;
import org.jacob.im.ifp.api.IFPParsingApi;
import org.jacob.im.obfo.constants.OBFOConstants;
import org.jacob.im.obfo.service.ReadAndMoveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Map;

/**
 * This class is responsible for handling the main execution logic
 * for reading YAML configuration files and moving files based on the specified paths.
 * It uses a {@link BufferedReader} to read user input from the console and processes the input
 * to perform file operations.
 *
 * @author Kotohiko
 * @since 14:38 Sep 12, 2024
 */
public class ReadAndMoveController {

    /**
     * The logger instance used for logging messages related to the {@link ReadAndMoveController} class.
     * This logger is configured to log messages at various levels (e.g., debug, info, error) and can be
     * used throughout the class to provide detailed information about the watcher's operations.
     */
    private static final Logger logger = LoggerFactory.getLogger(ReadAndMoveController.class);

    /**
     * The main execution method of the {@link ReadAndMoveController} class. This method handles user input,
     * reads YAML configuration files, and performs file moving operations based on the provided paths.
     */
    public static void mainPart() {
        System.out.print(OBFOConstants.WELCOME_LINE);

        try (BufferedReader in = IMCommonHelper.consoleReader()) {
            String cmd;

            while (true) {
                System.out.print(ResManager.loadResString("ReadAndMoveController_0"));
                if ((cmd = in.readLine()) == null) {
                    break;
                }

                var switchToIFP = IFPParsingApi.getAndParse(cmd);
                if (cmd.equals("check")) {
                    checkPathStatus();
                } else if (isValidPath(cmd)) {
                    openFolder(cmd);
                    System.out.println(IMCommonConstants.SEPARATOR_LINE);
                } else if (switchToIFP) {
                    System.out.println(IMCommonConstants.SEPARATOR_LINE);
                } else {
                    readYamlAndMoveFiles(cmd);
                }
            }
        } catch (IOException e) {
            logger.error(ResManager.loadResString("ReadAndMoveController_2"), e);
        }
        ReadAndMoveService.endLinePrintAndReboot();
    }

    /**
     * Checks the status of a specified path and prints out the names of any files found.
     * If no files are present, it provides feedback accordingly.
     */
    private static void checkPathStatus() {
        File directory = new File(OBFOConstants.ROCKET_LAUNCHER);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            // Check if there are actually files in the directory
            if (files != null && files.length > 0) {
                for (File file : files) {
                    System.out.println(file.getName());
                }
                // Provide feedback when no files are found
            } else {
                System.out.println("Buffer has no files yet.");
            }
        } else {
            System.out.println("Path does not exist or is not a valid directory path");
        }
    }

    /**
     * Opens a folder using the explorer.exe command.
     *
     * @param path The path of the folder to be opened.
     */
    private static void openFolder(String path) {
        try {
            // Create a ProcessBuilder instance with the command to execute
            var builder = new ProcessBuilder(OBFOConstants.EXPLORER_EXE, path);
            builder.start();
            logger.info(ResManager.loadResString("ReadAndMoveController_4", path));
        } catch (IOException e) {
            logger.error(ResManager.loadResString("ReadAndMoveController_3"));
        }
    }

    /**
     * Reads a YAML file to obtain paths data and moves files based on the provided target path key.
     * Logs an error if the default source path is not found or empty.
     *
     * @param targetPathKey the key used to identify the target path in the YAML data
     */
    private static void readYamlAndMoveFiles(String targetPathKey) {
        // Load a YAML file into a Java object.
        Map<String, String> pathsData = new Yaml().load(ReadAndMoveService.loadYamlFile());
        String defaultSourcePath = pathsData.get("Default source path");

        if (defaultSourcePath == null || defaultSourcePath.isEmpty()) {
            logger.error(ResManager.loadResString("ReadAndMoveController_1"));
        } else {
            ReadAndMoveService.defineSourcePathAndTargetPath(defaultSourcePath, pathsData, targetPathKey);
        }
    }

    /**
     * Checks if the given path is valid.
     *
     * @param path The path to validate.
     * @return true if the path is valid, false otherwise.
     */
    private static boolean isValidPath(String path) {
        try {
            var p = Paths.get(path);
            return Files.exists(p);
        } catch (InvalidPathException e) {
            return false;
        }
    }
}