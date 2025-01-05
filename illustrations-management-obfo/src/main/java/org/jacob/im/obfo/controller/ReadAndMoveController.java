package org.jacob.im.obfo.controller;

import org.jacob.im.common.constants.IMCommonConstants;
import org.jacob.im.common.controller.BaseController;
import org.jacob.im.common.helper.IMCommonHelper;
import org.jacob.im.common.response.ResManager;
import org.jacob.im.ifp.api.IFPParsingApi;
import org.jacob.im.obfo.command.UserCmd;
import org.jacob.im.obfo.command.impl.*;
import org.jacob.im.obfo.constants.OBFOConstants;
import org.jacob.im.obfo.service.ReadAndMoveService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * The ReadAndMoveController class handles user commands related to reading configuration files,
 * checking path statuses, opening folders, and moving files based on YAML configuration.
 * It employs the Command design pattern to delegate command execution to respective handlers.
 *
 * <p>This class interacts with the user via a console interface and processes input commands.
 * Supported commands include:
 * <ul>
 *   <li>Empty command: Executes the default command handler.</li>
 *   <li>{@code check}: Verifies and displays the status of a specified directory path.</li>
 *   <li>{@code open [path]}: Opens a folder at the specified path.</li>
 *   <li>Other commands: Reads YAML files and processes file operations.</li>
 * </ul>
 *
 * <p>Usage of this class involves calling the {@link #cmdHandler()} method to start the interaction loop.
 */
public class ReadAndMoveController extends BaseController {

    /**
     * A list of commands supported by this controller.
     */
    private final List<UserCmd> userCmds;

    /**
     * Constructs a new ReadAndMoveController and initializes its command handlers.
     * Each command is mapped to a specific action that this controller can perform.
     */
    public ReadAndMoveController() {
        userCmds = List.of(
                new EmptyCmd(this),
                new CheckCmd(this),
                new OpenCmd(this),
                new ShortOpenCmd(this),
                new ReadYamlCmd(this)
        );
    }

    /**
     * Starts the command handling loop, reading input from the console and delegating
     * execution to the appropriate command or fallback logic.
     */
    public void cmdHandler() {
        System.out.print(OBFOConstants.WELCOME_LINE);
        try (BufferedReader in = IMCommonHelper.consoleReader()) {
            String cmd;

            while (true) {
                System.out.print(ResManager.loadResString("ReadAndMoveController_0"));
                if ((cmd = in.readLine()) == null) {
                    break;
                }

                var switchToIFP = IFPParsingApi.getAndParse(cmd);
                cmd = cmd.trim();

                boolean handled = false;
                for (UserCmd userCmd : userCmds) {
                    if (userCmd.matches(cmd)) {
                        userCmd.execute(cmd);
                        handled = true;
                        break;
                    }
                }

                if (!handled && switchToIFP) {
                    System.out.println(IMCommonConstants.SEPARATOR_LINE);
                } else if (!handled) {
                    this.readYamlAndMoveFiles(cmd);
                }
            }
        } catch (IOException e) {
            logger.error(ResManager.loadResString("ReadAndMoveController_2"), e);
        }
        this.endLinePrintAndReboot();
    }

    /**
     * Checks the status of a specified directory path and lists the files it contains.
     * Provides feedback if the directory does not exist or is empty.
     */
    public void checkPathStatus() {
        try {
            Map<String, String> illustrationsPathMap = IMCommonHelper.getIllustrationsPathMap();
            String defaultSourcePath = illustrationsPathMap.get("Default source path");
            File directory = new File(defaultSourcePath);

            if (directory.exists() && directory.isDirectory()) {
                File[] files = directory.listFiles();

                if (files != null && files.length > 0) {
                    for (File file : files) {
                        System.out.println(file.getName());
                    }
                } else {
                    System.out.println("Buffer has no files yet.");
                }
            } else {
                System.out.println("Path does not exist or is not a valid directory path");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Opens a folder using the explorer.exe command.
     */
    public void openFolder(String path) {
        try {
            var builder = new ProcessBuilder(OBFOConstants.EXPLORER_EXE, path);
            builder.start();
            logger.info(ResManager.loadResString("ReadAndMoveController_4", path));
        } catch (IOException e) {
            logger.error(ResManager.loadResString("ReadAndMoveController_3"));
        }
    }

    /**
     * Reads a YAML configuration file and performs file operations based on the provided target path key.
     *
     * @param targetPathKey The key used to identify the target path in the YAML configuration.
     */
    public void readYamlAndMoveFiles(String targetPathKey) {
        try {
            Map<String, String> illustrationsPathMap = IMCommonHelper.getIllustrationsPathMap();
            String defaultSourcePath = illustrationsPathMap.get("Default source path");

            if (defaultSourcePath == null || defaultSourcePath.isEmpty()) {
                logger.error(ResManager.loadResString("ReadAndMoveController_1"));
            } else {
                new ReadAndMoveService().defineSourcePathAndTargetPath(defaultSourcePath, illustrationsPathMap, targetPathKey);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Prints a separator line and restarts the command handler loop.
     */
    public void endLinePrintAndReboot() {
        System.out.println(IMCommonConstants.SEPARATOR_LINE);
        cmdHandler();
    }

    /**
     * Validates if the given path exists and is accessible.
     *
     * @param path The path to validate.
     * @return True if the path exists, false otherwise.
     */
    public boolean isValidPath(String path) {
        try {
            var p = Paths.get(path);
            return Files.exists(p);
        } catch (InvalidPathException e) {
            return false;
        }
    }
}
