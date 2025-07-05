package org.jacob.obio.ifp.controller;

import org.jacob.obio.common.controller.BaseController;
import org.jacob.obio.common.response.ResManager;

import java.awt.*;
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
public class IFPEntranceController extends BaseController {
    /**
     * Opens the specified URI in the default browser.
     */
    public void openUriByBrowser(String out) {
        logger.info(ResManager.loadResString("IFPEntranceController_2", out));
        var desktop = Desktop.getDesktop();
        try {
            var uri = new URI(out);
            desktop.browse(uri);
        } catch (URISyntaxException | IOException e) {
            logger.error(ResManager.loadResString("IFPEntranceController_3"));
        }
    }
}
