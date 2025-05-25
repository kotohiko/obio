package org.jacob.im.obfo.command.impl;

import org.jacob.im.obfo.command.UserCmd;
import org.jacob.im.obfo.controller.ReadAndMoveController;

/**
 * Handles empty commands.
 */
public class EmptyCmd implements UserCmd {

    private final ReadAndMoveController controller;

    public EmptyCmd(ReadAndMoveController controller) {
        this.controller = controller;
    }

    @Override
    public boolean matches(String input) {
        return input.isEmpty();
    }

    @Override
    public void execute(String input) {
        controller.cmdHandler();
    }
}
