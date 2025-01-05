package org.jacob.im.ifp.api;

import org.jacob.im.ifp.controller.FilenameSwitcher;
import org.jacob.im.ifp.controller.IFPEntranceController;

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
            new IFPEntranceController().openUriByBrowser(retUrl);
            return true;
        }
    }
}