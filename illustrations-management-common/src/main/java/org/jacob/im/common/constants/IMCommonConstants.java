package org.jacob.im.common.constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Jacob Suen
 * @since 23:27 Aug 26, 2024
 */
public class IMCommonConstants {

    /**
     * Just one simple line of code is needed to obtain the standard format time.
     */
    public static final String NOW_DATE_TIME
            = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    public static final String SEPARATOR_LINE
            = "------------------------------------------------------------------------------------SEPARATOR LINE"
            + "------------------------------------------------------------------------------------";


}