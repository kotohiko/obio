package org.jacob.im.common.response;

import org.jacob.im.common.constants.ObioCommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * A utility class for managing resource strings loaded from a YAML configuration file.
 * Provides methods for retrieving and formatting messages based on keys defined in the YAML file.
 * Utilizes a logger for tracking operational status and errors.
 *
 * @author Kotohiko
 * @since 17:02 Sep 12, 2024
 */
public class ResManager {

    /**
     * The logger instance used for logging messages related to the {@link ResManager} class.
     * This logger is configured to log messages at various levels (e.g., debug, info, error) and can be
     * used throughout the class to provide detailed information about the watcher's operations.
     */
    private static final Logger logger = LoggerFactory.getLogger(ResManager.class);

    /**
     * Loads a resource string based on the message key and replaces placeholders.
     *
     * @param msgKey      The message key used to find the corresponding string in the YAML file.
     * @param placeholder Variable arguments used to replace placeholders in the message string.
     * @return The processed message string with placeholders replaced.
     */
    public static String loadResString(String msgKey, String... placeholder) {
        Yaml yaml = new Yaml();
        FileInputStream ymlFileStream = null;
        try {
            ymlFileStream = new FileInputStream(ObioCommonConstants.MESSAGES_YAML_PATH);
        } catch (FileNotFoundException e) {
            logger.error(ResManager.loadResString("ResManager_0"));
        }

        Map<String, String> pathsData = yaml.load(ymlFileStream);
        // Retrieve the corresponding string based on the message key
        String msg = pathsData.get(msgKey);
        // Process placeholders
        if (placeholder.length > 0) {
            for (var i = 0; i < placeholder.length; ++i) {
                msg = msg.replace("{" + i + "}", placeholder[i]);
            }
        }
        return msg;
    }
}