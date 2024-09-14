package org.jacob.im.ifp.controller;

import org.jacob.im.common.constants.IMCommonConstants;
import org.jacob.im.common.helper.IMCommonHelper;
import org.jacob.im.common.response.ResManager;
import org.jacob.im.ifp.service.FilenameSwitcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * This class serves as the entry point for parsing filenames into URLs.
 * It handles user input from the console, processes the filenames, and opens the resulting URLs in a browser.
 *
 * @author Kotohiko
 * @since Oct 2, 2023
 */
public final class IFPEntranceController {

    /**
     * The logger instance used for logging messages related to the {@link IFPEntranceController} class.
     * This logger is configured to log messages at various levels (e.g., debug, info, error) and can be
     * used throughout the class to provide detailed information about the watcher's operations.
     */
    private static final Logger logger = LoggerFactory.getLogger(IFPEntranceController.class);

    /**
     * Don't let anyone instantiate this class
     */
    private IFPEntranceController() {
    }

    /**
     * Prompts the user for a filename, processes it to extract a URL, and opens the URL in a browser.
     * If the filename does not yield a valid URL, it logs a warning and prompts the user again.
     */
    public static void getFilename() {
        try (BufferedReader in = IMCommonHelper.consoleReader()) {
            String fileName;
            System.out.print(ResManager.loadResString("IFPEntranceController_1"));
            while ((fileName = in.readLine()) != null) {
                String retUrl = FilenameSwitcher.parseFileName(fileName);
                if (retUrl.isBlank()) {
                    logger.warn(ResManager.loadResString("IFPEntranceController_0"));
                    endLinePrintAndReboot();
                } else {
                    openUriByBrowser(retUrl);
                    endLinePrintAndReboot();
                    System.out.print(ResManager.loadResString("IFPEntranceController_1"));
                }
            }
        } catch (IOException e) {
            logger.error(ResManager.loadResString("IFPEntranceController_4"));
        }
        endLinePrintAndReboot();
    }

    /**
     * Opens the specified URI in the default browser.
     */
    private static void openUriByBrowser(String out) {
        logger.info(ResManager.loadResString("IFPEntranceController_2", out));
        Desktop desktop = Desktop.getDesktop();
        try {
            URI uri = new URI(out);
            desktop.browse(uri);
        } catch (URISyntaxException | IOException e) {
            logger.error(ResManager.loadResString("IFPEntranceController_3"));
        }
        endLinePrintAndReboot();
    }

    /**
     * Prints a separator line and then calls {@link IFPEntranceController#getFilename()} to prompt the user again.
     */
    private static void endLinePrintAndReboot() {
        System.out.println(IMCommonConstants.SEPARATOR_LINE);
        getFilename();
    }
}
