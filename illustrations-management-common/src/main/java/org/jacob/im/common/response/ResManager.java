package org.jacob.im.common.response;

import org.jacob.im.common.constants.IMCommonConstants;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * @author Kotohiko
 * @since 17:02 Sep 12, 2024
 */
public class ResManager {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ResManager.class);

    public static String loadResString(String msgKey, String... placeholder) {
        Yaml yaml = new Yaml();
        FileInputStream ymlFileStream = null;
        try {
            ymlFileStream = new FileInputStream(IMCommonConstants.MESSAGES_YAML_PATH);
        } catch (FileNotFoundException e) {
            logger.error(ResManager.loadResString("ResManager_0"));
        }
        Map<String, String> pathsData = yaml.load(ymlFileStream);
        String msg = pathsData.get(msgKey);
        // 处理占位符
        if (placeholder.length > 0) {
            for (var i = 0; i < placeholder.length; ++i) {
                msg = msg.replace("{" + i + "}", placeholder[i]);
            }
        }
        return msg;
    }
}