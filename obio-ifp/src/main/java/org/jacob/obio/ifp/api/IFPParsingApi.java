package org.jacob.obio.ifp.api;

import org.jacob.obio.ifp.controller.FileNameParserService;
import org.jacob.obio.ifp.controller.IFPEntranceController;

/**
 * @author Kotohiko
 * @since 09:30 Sep 16, 2024
 */
public class IFPParsingApi {

    private final FileNameParserService fileNameParserService;

    public IFPParsingApi(FileNameParserService fileNameParserService) {
        this.fileNameParserService = fileNameParserService;
    }

    public boolean getAndParse(String fileName) {
        String retUrl = fileNameParserService.parseFileName(fileName);
        if (retUrl.isBlank()) {
            return false;
        } else {
            new IFPEntranceController().openUriByBrowser(retUrl);
            return true;
        }
    }
}