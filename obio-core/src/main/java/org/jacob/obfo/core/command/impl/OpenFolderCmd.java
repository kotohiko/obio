package org.jacob.obfo.core.command.impl;

import org.jacob.obfo.core.command.UserCmd;
import org.jacob.obfo.core.controller.ReadAndMoveController;

/**
 * Handles {@code open} command.
 */
public record OpenFolderCmd(ReadAndMoveController controller) implements UserCmd {

    @Override
    public boolean matches(String input) {
        return input.startsWith("open ") && controller.isValidPath(input.substring(5));
    }

    @Override
    public void execute(String input) {
        controller.openFolder(input.substring(5));
    }
}
