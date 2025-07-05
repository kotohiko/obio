package org.jacob.obio.common.helper;

import org.jacob.obio.common.constants.ObioCommonConstants;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Utility class providing common helper functions for IM applications.
 * Includes methods for obtaining the current timestamp in a standard format and reading from the console etc.
 *
 * @author Kotohiko
 * @since 19:06 Sep 08, 2024
 */
public class ObioCommonHelper {

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

    public static Map<String, String> getIllustrationsPathMap() throws IOException {
        FileInputStream ymlFileStream;

        try {
            ymlFileStream = new FileInputStream(ObioCommonConstants.ILLUSTRATIONS_CONF_YML_PATH);
        } catch (FileNotFoundException e) {
            throw new IOException("ReadAndMoveService_1");
        }
        return new Yaml().load(ymlFileStream);

    }
}