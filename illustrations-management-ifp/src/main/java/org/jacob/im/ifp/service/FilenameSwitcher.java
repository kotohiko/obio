package org.jacob.im.ifp.service;

import org.jacob.im.ifp.constants.IFPConstants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Get input contents from client (console or front-end).
 *
 * @author Kotohiko
 * @since Oct 2, 2023
 */
public final class FilenameSwitcher {

    /**
     * Don't let anyone instantiate this class
     */
    private FilenameSwitcher() {
    }

    /**
     * Filename parsing method entrance.
     *
     * @param fileName filename
     * @return parsed URL
     */
    public static String parseFileName(String fileName) {
        var pixivMatcher = Pattern.compile(IFPConstants.PIXIV_PATTERN).matcher(fileName);
        var pixivMatcher2 = Pattern.compile(IFPConstants.PIXIV_PATTERN_2).matcher(fileName);
        if (pixivMatcher.find() || pixivMatcher2.find()) {
            return FilenameParser.pixivIllustrationsOrIllustratorIdParser(fileName);
        } else if (fileName.contains("httpstwitter") || fileName.contains("httpsx")) {
            return FilenameParser.twitterParser(fileName);
        } else if (fileName.contains("httpsdanbooru")) {
            return FilenameParser.danbooruParser(fileName);
        } else if (fileName.contains("bilibili")) {
            return FilenameParser.bilibiliIllustrationsParser(fileName);
        } else if (fileName.contains("miyoushe.com")) {
            return FilenameParser.miyousheParser(fileName);
        } else if (fileName.contains("deviantart")) {
            return FilenameParser.deviantartParser(fileName);
        }
        return "";
    }
}
