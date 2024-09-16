package org.jacob.im.ifp.api;

import org.jacob.im.ifp.controller.IFPEntranceController;
import org.jacob.im.ifp.service.FilenameSwitcher;

/**
 * @author Kotohiko
 * @since 09:30 Sep 16, 2024
 */
public class IFPParsingApi {

    public static boolean getAndParse(String fileName) {
        String retUrl = FilenameSwitcher.parseFileName(fileName);
        if (retUrl.isBlank()) {
            return false;
        } else {
            IFPEntranceController.openUriByBrowser(retUrl);
            return true;
        }
    }
}