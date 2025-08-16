package org.jacob.obfo.core.command.impl;

import org.jacob.obfo.core.command.UserCmd;
import org.jacob.obfo.core.controller.ReadAndMoveController;

/**
 * Handles "check" command.
 */
public record CheckCmd(ReadAndMoveController controller) implements UserCmd {

    @Override
    public boolean matches(String input) {
        return "check".equals(input);
    }

    @Override
    public void execute(String input) {
        controller.checkPathStatus();
    }
}
