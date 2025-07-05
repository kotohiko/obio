package org.jacob.obio.ifp.service;

/**
 * Provides utilities for parsing filenames into URLs based on different platforms.
 *
 * @author Kotohiko
 * @since Oct 29, 2023
 */
public final class FilenameParser {

    /**
     * Converts the specified string to a StringBuilder object.
     *
     * @param string The string to be converted.
     * @return A StringBuilder object representing the input string.
     */
    private StringBuilder getStringBuilder(String string) {
        return new StringBuilder(string);
    }

    /**
     * <a href="https://www.pixiv.net/">Pixiv</a> URL-like filename parser.
     *
     * @param str filename string param
     * @return parsed URL
     */
    public String pixivIllustrationsOrIllustratorIdParser(String str) {
        return String.format("https://www.pixiv.net/artworks/%s", str.substring(0, str.indexOf('_')));
    }

    /**
     * <a href="https://twitter.com/">Twitter</a> URL-like filename parser.
     *
     * @param str filename string param
     * @return parsed URL
     */
    public String twitterParser(String str) {
        var sb = getStringBuilder(str);
        if (str.contains("httpstwitter.com")) {
            sb.replace(0, 16, "https://x.com/");
        } else if (str.contains("httpsx.com")) {
            sb.replace(0, 10, "https://x.com/");
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
    public String yandeParser(String str) {
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
    public String miyousheParser(String str) {
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
    public String danbooruParser(String str) {
        var sb = getStringBuilder(str);
        return sb.replace(0, 28, "https://danbooru.donmai.us/posts/").toString();
    }

    /**
     * <a href="https://www.bilibili.com/">Bilibili</a> URL-like filename parser.
     *
     * @param str filename string param
     * @return parsed URL
     */
    public String bilibiliIllustrationsParser(String str) {
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
    public String bilibiliVideosParser(String str) {
        var sb = getStringBuilder(str);
        sb.insert(5, "://");
        sb.insert(sb.indexOf("video"), "/");
        sb.insert(sb.indexOf("video") + 5, "/");
        return sb.toString();
    }

    @SuppressWarnings("unused")
    public String baiduNetDiskParser(String str) {
        return "pan.baidu.com/s/" + str;
    }

    /**
     * <a href="https://www.nicovideo.jp/">Niconico</a> URL-like filename parser.
     *
     * @param str filename string param
     * @return parsed URL
     */
    @SuppressWarnings("unused")
    public String nicoVideoParser(String str) {
        return "https://seiga.nicovideo.jp/seiga/" + str;
    }

    /**
     * <a href="https://alphacoders.com/">Alpha Coders</a> URL-like filename parser.
     *
     * @param str filename string param
     * @return parsed URL
     */
    @SuppressWarnings("unused")
    public String alphacodersParser(String str) {
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
    public String youtubeParser(String str) {
        var sb = getStringBuilder(str);
        if (str.contains("httpswww.youtube.comwatch")) {
            sb.replace(0, 27, "https://www.youtube.com/watch?v=");
        } else if (str.contains("httpsyoutu.be")) {
            sb.replace(0, 13, "https://youtu.be/");
        } else if (str.contains("httpswww.youtube.comshorts")) {
            sb.replace(0, 26, "https://www.youtube.com/shorts/");
        }
        return sb.toString();
    }

    /**
     * <a href="https://wallpapercave.com/">Wallpaper Cave</a> URL-like filename parser.
     *
     * @param str filename string param
     * @return parsed URL
     */
    @SuppressWarnings("unused")
    public String wallpaperCaveParser(String str) {
        return "https://wallpapercave.com/w/" + str;
    }

    /**
     * <a href="https://www.deviantart.com/">Deviantart</a> URL-like filename parser.
     *
     * @param str filename string param
     * @return parsed URL
     */
    public String deviantartParser(String str) {
        var sb = getStringBuilder(str);
        sb.insert(5, "://");
        sb.insert(sb.indexOf("com") + 3, "/");
        sb.insert(sb.indexOf("sevenics") + 8, "/");
        sb.insert(sb.lastIndexOf("art") + 3, "/");
        return sb.toString();
    }

    /**
     * <a href="https://www.facebook.com/">Facebook</a> URL-like filename parser.
     *
     * @param str filename string param
     * @return parsed URL
     */
    public String facebookParser(String str) {
        var sb = getStringBuilder(str);
        sb.insert(5, "://");
        sb.insert(sb.indexOf("com") + 3, "/");
        sb.insert(sb.indexOf("photo") + 5, "/");
        sb.insert(sb.indexOf("photo") + 6, "?");
        return sb.toString();
    }
}
