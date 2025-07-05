package org.jacob.obfo.core.command.impl;

import org.jacob.obfo.core.command.UserCmd;
import org.jacob.obfo.core.controller.ReadAndMoveController;
import org.jacob.obio.common.helper.ObioCommonHelper;
import org.jacob.obio.common.response.ResManager;

import java.io.IOException;
import java.util.Map;

/**
 * @author Kotohiko
 * @since 13:40 Jan 05, 2025
 */
public class ShortOpenFolderCmd implements UserCmd {

    private final ReadAndMoveController controller;

    public ShortOpenFolderCmd(ReadAndMoveController controller) {
        this.controller = controller;
    }

    @Override
    public boolean matches(String input) {
        return input.startsWith("open -s ");
    }

    @Override
    public void execute(String input) {
        try {
            String shortPath = input.substring(8);
            Map<String, String> illustrationsPathMap = ObioCommonHelper.getIllustrationsPathMap();
            if (null != illustrationsPathMap.get(shortPath)) {
                controller.openFolder(illustrationsPathMap.get(shortPath));
            } else {
                System.out.println(ResManager.loadResString("ShortOpenCmd_0", shortPath));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}