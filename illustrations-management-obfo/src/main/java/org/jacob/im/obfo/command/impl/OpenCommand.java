package org.jacob.im.obfo.command.impl;

import org.jacob.im.obfo.command.Command;
import org.jacob.im.obfo.controller.ReadAndMoveController;

/**
 * Handles "open" command.
 */
public class OpenCommand implements Command {

    private final ReadAndMoveController controller;

    public OpenCommand(ReadAndMoveController controller) {
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
