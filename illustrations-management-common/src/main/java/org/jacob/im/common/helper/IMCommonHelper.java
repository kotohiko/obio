package org.jacob.im.common.helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Kotohiko
 * @since 19:06 Sep 08, 2024
 */
public class IMCommonHelper {

    /**
     * Just one simple line of code is needed to obtain real-time time in standard format.
     */
    public static String getRealTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * Common console reader.
     *
     * @return A {@link BufferedReader} object.
     */
    public static BufferedReader consoleReader() {
        return new BufferedReader(new InputStreamReader(System.in));
    }
}