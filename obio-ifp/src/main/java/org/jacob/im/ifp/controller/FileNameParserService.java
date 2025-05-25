package org.jacob.im.ifp.controller;

import org.jacob.im.ifp.constants.IFPConstants;
import org.jacob.im.ifp.service.FilenameParser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Get input contents from client (console or front-end).
 *
 * @author Kotohiko
 * @since Oct 2, 2023
 */
public final class FileNameParserService {

    private static final Pattern PIXIV_MATCHER = Pattern.compile(IFPConstants.PIXIV_PATTERN);
    private static final Pattern PIXIV_MATCHER_2 = Pattern.compile(IFPConstants.PIXIV_PATTERN_2);
    private final Map<String, Function<String, String>> parserMap;
    private final FilenameParser filenameParser;

    public FileNameParserService() {
        this.filenameParser = new FilenameParser();
        this.parserMap = new HashMap<>();

        // Initialize the parser map with conditions and corresponding parsers
        parserMap.put("httpstwitter", filenameParser::twitterParser);
        parserMap.put("httpsx", filenameParser::twitterParser);
        parserMap.put("httpsdanbooru", filenameParser::danbooruParser);
        parserMap.put("bilibili.comopus", filenameParser::bilibiliIllustrationsParser);
        parserMap.put("miyoushe.com", filenameParser::miyousheParser);
        parserMap.put("deviantart", filenameParser::deviantartParser);
        parserMap.put("facebook", filenameParser::facebookParser);
        parserMap.put("bilibili.comvideo", filenameParser::bilibiliVideosParser);
        parserMap.put("youtube", filenameParser::youtubeParser);
    }

    public String parseFileName(String fileName) {
        // Handle special cases with regex first
        if (PIXIV_MATCHER.matcher(fileName).find() || PIXIV_MATCHER_2.matcher(fileName).find()) {
            return filenameParser.pixivIllustrationsOrIllustratorIdParser(fileName);
        }

        // Use the parser map to find the correct parser
        for (Map.Entry<String, Function<String, String>> entry : parserMap.entrySet()) {
            if (fileName.contains(entry.getKey())) {
                return entry.getValue().apply(fileName);
            }
        }

        // Default case if no conditions are matched
        return "";
    }
}
