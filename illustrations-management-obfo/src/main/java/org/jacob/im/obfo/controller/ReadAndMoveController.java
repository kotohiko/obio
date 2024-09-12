package org.jacob.im.obfo.controller;

import org.jacob.im.common.helper.IMCommonHelper;
import org.jacob.im.common.response.ResManager;
import org.jacob.im.obfo.constants.OBFOConstants;
import org.jacob.im.obfo.service.ReadAndMoveService;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * @author Kotohiko
 * @since 14:38 Sep 12, 2024
 */
public class ReadAndMoveController {

    /**
     * Main Execution Method
     */
    public static void mainPart() {
        System.out.print(OBFOConstants.WELCOME_LINE);
        try (BufferedReader in = IMCommonHelper.consoleReader()) {
            String targetPathKey;
            Yaml yaml = new Yaml();
            while (true) {
                System.out.print(ResManager.loadResString("ReadAndMoveController_0"));
                if ((targetPathKey = in.readLine()) == null) {
                    break;
                }
                // Load a YAML file into a Java object.
                Map<String, String> pathsData = yaml.load(ReadAndMoveService.loadYamlFile());
                String defaultSourcePath = pathsData.get("Default source path");
                if (defaultSourcePath == null || defaultSourcePath.isEmpty()) {
                    System.out.println(ResManager.loadResString("ReadAndMoveController_1"));
                } else {
                    ReadAndMoveService.filesMove(defaultSourcePath, pathsData, targetPathKey);
                }
            }
        } catch (IOException e) {
            System.out.println(ResManager.loadResString("ReadAndMoveController_2"));
        }
        ReadAndMoveService.endLinePrintAndReboot();
    }
}