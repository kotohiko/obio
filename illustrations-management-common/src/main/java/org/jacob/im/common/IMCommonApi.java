package org.jacob.im.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Kotohiko
 * @since 19:06 Sep 08, 2024
 */
public class IMCommonApi {
    /**
     * Just one simple line of code is needed to obtain the standard format time.
     */
    public static String getRealTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}