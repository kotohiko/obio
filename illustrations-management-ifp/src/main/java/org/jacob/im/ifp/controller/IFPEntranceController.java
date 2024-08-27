package org.jacob.im.ifp.controller;

import org.jacob.im.common.IMCommonConsoleInputReader;
import org.jacob.im.common.constants.IMCommonConstants;
import org.jacob.im.ifp.service.FilenameSwitcher;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Entrance of parsing filename to URL.
 *
 * @author Jacob Suen
 * @since Oct 2, 2023
 */
public final class IFPEntranceController {

    /**
     * Don't let anyone instantiate this class
     */
    private IFPEntranceController() {
    }

    /**
     * 交互逻辑
     */
    public static void getFilename() {
        try (BufferedReader in = IMCommonConsoleInputReader.consoleReader()) {
            String fileName;
            System.out.print("Please enter your filename: ");
            while ((fileName = in.readLine()) != null) {
                String retUrl = FilenameSwitcher.parseFileName(fileName);
                if (retUrl.isBlank()) {
                    System.out.println("The return URL value is blank, please verify that there are no errors or "
                            + "illegal characters in the input contents.");
                    endLinePrintAndReboot();
                } else {
                    openUriByBrowser(retUrl);
                    endLinePrintAndReboot();
                    System.out.print("Please enter your filename: ");
                }
            }
        } catch (IOException e) {
            System.out.println("发生了错误");
        }
        endLinePrintAndReboot();
    }

    /**
     * 支持解析以后直接通过浏览器来打开
     */
    private static void openUriByBrowser(String out) {
        System.out.println("Parsed successfully! The returned URL is: " + out + ". Your default browser will open"
                + " this URL automatically.");
        Desktop desktop = Desktop.getDesktop();
        try {
            URI uri = new URI(out);
            desktop.browse(uri);
        } catch (URISyntaxException | IOException e) {
            System.out.println("An error occurred in the URL syntax, or the URL is null. Please verify that "
                    + "there are no errors or illegal characters in the input contents.");
        }
        endLinePrintAndReboot();
    }

    private static void endLinePrintAndReboot() {
        System.out.println(IMCommonConstants.SEPARATOR_LINE);
        getFilename();
    }
}
