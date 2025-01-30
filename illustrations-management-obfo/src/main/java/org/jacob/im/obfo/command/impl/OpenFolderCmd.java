package org.jacob.im.obfo.command.impl;

import org.jacob.im.obfo.command.UserCmd;
import org.jacob.im.obfo.controller.ReadAndMoveController;

/**
 * Handles {@code open} command.
 */
public class OpenFolderCmd implements UserCmd {

    private final ReadAndMoveController controller;

    public OpenFolderCmd(ReadAndMoveController controller) {
        this.controller = controller;
    }

    @Override
    public boolean matches(String input) {
        return input.startsWith("open ") && controller.isValidPath(input.substring(5));
    }

    @Override
    public void execute(String input) {
        controller.openFolder(input.substring(5));
    }
}
