package org.jacob.im.ifp.service;

/**
 * Core class for parsing filenames.
 *
 * @author Jacob Suen
 * @since Oct 29, 2023
 */
public final class FilenameParser {

    /**
     * Don't let anyone instantiate this class.
     */
    private FilenameParser() {
    }

    private static StringBuilder getStringBuilder(String string) {
        return new StringBuilder(string);
    }

    /**
     * <a href="https://www.pixiv.net/">Pixiv</a> URL-like filename parser.
     *
     * @param str filename string param
     * @return parsed URL
     */
    public static String pixivIllustrationsOrIllustratorIdParser(String str) {
        if (str.contains("_")) {
            str = str.substring(0, str.indexOf('_'));
            return "https://www.pixiv.net/artworks/" + str;
        } else {
            return "https://www.pixiv.net/users/" + str;
        }
    }

    /**
     * <a href="https://twitter.com/">Twitter</a> URL-like filename parser.
     *
     * @param str filename string param
     * @return parsed URL
     */
    public static String twitterParser(String str) {
        var sb = getStringBuilder(str);
        sb.insert(5, "://");
        if (str.contains("twitter")) {
            sb.insert(19, "/");
        } else if (str.contains("httpsx.com")) {
            sb.insert(13, "/");
        }
        sb.insert(sb.indexOf("status"), "/");
        sb.insert(sb.indexOf("status") + 6, "/");
        if (str.endsWith("photo1")) {
            return sb.substring(0, sb.indexOf("photo1"));
        } else if (str.endsWith("photo2")) {
            return sb.substring(0, sb.indexOf("photo2"));
        } else if (str.endsWith("photo3")) {
            return sb.substring(0, sb.indexOf("photo3"));
        } else if (str.endsWith("photo4")) {
            return sb.substring(0, sb.indexOf("photo4"));
        } else {
            return sb.toString();
        }
    }

    /**
     * <a href="https://www.pixiv.net/">Yande</a> URL-like filename parser.
     *
     * @param str filename string param
     * @return parsed URL
     */
    @SuppressWarnings("unused")
    public static String yandeParser(String str) {
        var sb = getStringBuilder(str);
        sb.insert(5, "://");
        sb.insert(16, "/");
        sb.insert(21, "/");
        sb.insert(26, "/");
        return String.valueOf(sb);
    }

    /**
     * <a href="https://www.miyoushe.com/">Miyoushe</a> URL-like filename parser.
     *
     * @param str filename string param
     * @return parsed URL
     */
    public static String miyousheParser(String str) {
        var sb = getStringBuilder(str);
        sb.insert(5, "://");
        sb.insert(24, "/");
        if (str.contains("ys")) {
            sb.insert(sb.indexOf("ys") + 2, "/");
        } else if (str.contains("bh3")) {
            sb.insert(sb.indexOf("bh3") + 3, "/");
        } else if (str.contains("sr")) {
            sb.insert(sb.indexOf("sr") + 2, "/");
        }
        sb.insert(sb.indexOf("article") + 7, "/");
        return sb.toString();
    }

    /**
     * <a href="https://danbooru.donmai.us/">Danbooru</a> URL-like filename parser.
     *
     * @param str filename string param
     * @return parsed URL
     */
    public static String danbooruParser(String str) {
        var sb = getStringBuilder(str);
        sb.insert(5, "://");
        sb.insert(26, "/");
        sb.insert(32, "/");
        return String.valueOf(sb);
    }

    /**
     * <a href="https://www.bilibili.com/">Bilibili</a> URL-like filename parser.
     *
     * @param str filename string param
     * @return parsed URL
     */
    public static String bilibiliIllustrationsParser(String str) {
        var sb = getStringBuilder(str);
        sb.insert(5, "://");
        if (str.contains("opus")) {
            sb.insert(24, "/");
            sb.insert(sb.indexOf("opus") + 4, "/");
        } else if (str.contains("t.bilibili")) {
            sb.insert(22, "/");
        }
        if (str.contains("spm_id_from")) {
            sb.delete(sb.indexOf("spm_id_from"), sb.length());
        }
        return sb.toString();
    }

    /**
     * <a href="https://www.bilibili.com/">Bilibili</a> URL-like filename parser.
     *
     * @param str filename string param
     * @return parsed URL
     */
    @SuppressWarnings("unused")
    public static String bilibiliVideosParser(String str) {
        var sb = getStringBuilder(str);
        sb.insert(5, "://");
        sb.insert(sb.indexOf("video"), "/");
        sb.insert(sb.indexOf("video") + 5, "/");
        return sb.toString();
    }

    @SuppressWarnings("unused")
    public static String baiduNetDiskParser(String str) {
        return "pan.baidu.com/s/" + str;
    }

    /**
     * <a href="https://www.nicovideo.jp/">Niconico</a> URL-like filename parser.
     *
     * @param str filename string param
     * @return parsed URL
     */
    @SuppressWarnings("unused")
    public static String nicoVideoParser(String str) {
        return "https://seiga.nicovideo.jp/seiga/" + str;
    }

    /**
     * <a href="https://alphacoders.com/">Alpha Coders</a> URL-like filename parser.
     *
     * @param str filename string param
     * @return parsed URL
     */
    @SuppressWarnings("unused")
    public static String alphacodersParser(String str) {
        var sb = getStringBuilder(str);
        sb.insert(5, "://");
        sb.insert(sb.indexOf("com") + 3, "/");
        sb.insert(sb.indexOf("i="), "?");
        return sb.toString();
    }

    /**
     * <a href="https://www.youtube.com/">YouTube</a> URL-like filename parser.
     *
     * @param str filename string param
     * @return parsed URL
     */
    @SuppressWarnings("unused")
    public static String youtubeParser(String str) {
        var sb = getStringBuilder(str);
        sb.insert(5, "://");
        sb.insert(sb.indexOf("youtu.be") + 8, "/");
        return sb.toString();
    }

    /**
     * <a href="https://wallpapercave.com/">Wallpaper Cave</a> URL-like filename parser.
     *
     * @param str filename string param
     * @return parsed URL
     */
    @SuppressWarnings("unused")
    public static String wallpaperCaveParser(String str) {
        return "https://wallpapercave.com/w/" + str;
    }
}
