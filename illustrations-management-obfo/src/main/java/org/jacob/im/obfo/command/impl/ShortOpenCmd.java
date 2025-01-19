package org.jacob.im.obfo.command.impl;

import org.jacob.im.common.helper.IMCommonHelper;
import org.jacob.im.common.response.ResManager;
import org.jacob.im.obfo.command.UserCmd;
import org.jacob.im.obfo.controller.ReadAndMoveController;

import java.io.IOException;
import java.util.Map;

/**
 *
 * @author Kotohiko
 * @since 13:40 Jan 05, 2025
 */
public class ShortOpenCmd implements UserCmd {

    private final ReadAndMoveController controller;

    public ShortOpenCmd(ReadAndMoveController controller) {
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
            Map<String, String> illustrationsPathMap = IMCommonHelper.getIllustrationsPathMap();
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