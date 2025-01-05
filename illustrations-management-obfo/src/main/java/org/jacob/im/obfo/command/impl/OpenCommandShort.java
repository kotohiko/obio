package org.jacob.im.obfo.command.impl;

import org.jacob.im.common.helper.IMCommonHelper;
import org.jacob.im.obfo.command.Command;
import org.jacob.im.obfo.controller.ReadAndMoveController;

import java.io.IOException;
import java.util.Map;

/**
 *
 * @author Kotohiko
 * @since 13:40 Jan 05, 2025
 */
public class OpenCommandShort implements Command {

    private final ReadAndMoveController controller;

    public OpenCommandShort(ReadAndMoveController controller) {
        this.controller = controller;
    }

    @Override
    public boolean matches(String input) {
        return input.startsWith("open -s ");
    }

    @Override
    public void execute(String input) {
        try {
            Map<String, String> illustrationsPathMap = IMCommonHelper.getIllustrationsPathMap();
            controller.openFolder(illustrationsPathMap.get(input.substring(8)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}