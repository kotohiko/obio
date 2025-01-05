package org.jacob.im.obfo.command.impl;

import org.jacob.im.obfo.command.Command;
import org.jacob.im.obfo.controller.ReadAndMoveController;

/**
 * Handles empty commands.
 */
public class EmptyCommand implements Command {

    private final ReadAndMoveController controller;

    public EmptyCommand(ReadAndMoveController controller) {
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
