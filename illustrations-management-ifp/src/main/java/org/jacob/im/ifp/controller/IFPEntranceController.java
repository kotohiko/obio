package org.jacob.im.ifp.controller;

import org.jacob.im.common.constants.IMCommonConstants;
import org.jacob.im.common.helper.IMCommonHelper;
import org.jacob.im.common.response.ResManager;
import org.jacob.im.ifp.service.FilenameSwitcher;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Entrance of parsing filename to URL.
 *
 * @author Kotohiko
 * @since Oct 2, 2023
 */
public final class IFPEntranceController {

    /**
     * Don't let anyone instantiate this class
     */
    private IFPEntranceController() {
    }

    /**
     * Get the filename and process the core logic.
     */
    public static void getFilename() {
        try (BufferedReader in = IMCommonHelper.consoleReader()) {
            String fileName;
            System.out.print(ResManager.loadResString("IFPEntranceController_1"));
            while ((fileName = in.readLine()) != null) {
                String retUrl = FilenameSwitcher.parseFileName(fileName);
                if (retUrl.isBlank()) {
                    System.out.println(ResManager.loadResString("IFPEntranceController_0"));
                    endLinePrintAndReboot();
                } else {
                    openUriByBrowser(retUrl);
                    endLinePrintAndReboot();
                    System.out.print(ResManager.loadResString("IFPEntranceController_1"));
                }
            }
        } catch (IOException e) {
            System.out.println(ResManager.loadResString("IFPEntranceController_4"));
        }
        endLinePrintAndReboot();
    }

    /**
     * Supports opening directly through the browser after parsing.
     */
    private static void openUriByBrowser(String out) {
        System.out.println(ResManager.loadResString("IFPEntranceController_2", out));
        Desktop desktop = Desktop.getDesktop();
        try {
            URI uri = new URI(out);
            desktop.browse(uri);
        } catch (URISyntaxException | IOException e) {
            System.out.println(ResManager.loadResString("IFPEntranceController_3"));
        }
        endLinePrintAndReboot();
    }

    private static void endLinePrintAndReboot() {
        System.out.println(IMCommonConstants.SEPARATOR_LINE);
        getFilename();
    }
}
