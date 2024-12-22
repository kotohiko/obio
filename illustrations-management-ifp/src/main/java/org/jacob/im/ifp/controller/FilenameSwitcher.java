package org.jacob.im.ifp.controller;

import org.jacob.im.ifp.constants.IFPConstants;
import org.jacob.im.ifp.service.FilenameParser;

import java.util.regex.Pattern;

/**
 * Get input contents from client (console or front-end).
 *
 * @author Kotohiko
 * @since Oct 2, 2023
 */
public final class FilenameSwitcher {

    private static final Pattern PIXIV_MATCHER = Pattern.compile(IFPConstants.PIXIV_PATTERN);
    private static final Pattern PIXIV_MATCHER_2 = Pattern.compile(IFPConstants.PIXIV_PATTERN_2);

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
        var pixivMatched = PIXIV_MATCHER.matcher(fileName).find();
        var pixivMatched2 = PIXIV_MATCHER_2.matcher(fileName).find();

        if (pixivMatched || pixivMatched2) {
            return FilenameParser.pixivIllustrationsOrIllustratorIdParser(fileName);
        } else if (fileName.contains("httpstwitter") || fileName.contains("httpsx")) {
            return FilenameParser.twitterParser(fileName);
        } else if (fileName.contains("httpsdanbooru")) {
            return FilenameParser.danbooruParser(fileName);
        } else if (fileName.contains("bilibili.comopus")) {
            return FilenameParser.bilibiliIllustrationsParser(fileName);
        } else if (fileName.contains("miyoushe.com")) {
            return FilenameParser.miyousheParser(fileName);
        } else if (fileName.contains("deviantart")) {
            return FilenameParser.deviantartParser(fileName);
        } else if (fileName.contains("facebook")) {
            return FilenameParser.facebookParser(fileName);
        } else if (fileName.contains("bilibili.comvideo")) {
            return FilenameParser.bilibiliVideosParser(fileName);
        } else if (fileName.contains("youtube"))
            return FilenameParser.youtubeParser(fileName);
        return "";
    }
}
