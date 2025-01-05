package org.jacob.im.obfo.command.impl;

import org.jacob.im.obfo.command.Command;
import org.jacob.im.obfo.controller.ReadAndMoveController;

/**
 * Handles "check" command.
 */
public class CheckCommand implements Command {

    private final ReadAndMoveController controller;

    public CheckCommand(ReadAndMoveController controller) {
        this.controller = controller;
    }

    @Override
    public boolean matches(String input) {
        return "check".equals(input);
    }

    @Override
    public void execute(String input) {
        controller.checkPathStatus();
    }
}
