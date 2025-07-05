package org.jacob.obfo.core.command.impl;

import org.jacob.obfo.core.command.UserCmd;
import org.jacob.obfo.core.controller.ReadAndMoveController;

/**
 * Handles YAML processing commands.
 */
public class ReadYamlCmd implements UserCmd {

    private final ReadAndMoveController controller;

    public ReadYamlCmd(ReadAndMoveController controller) {
        this.controller = controller;
    }

    @Override
    public boolean matches(String input) {
        return !input.isEmpty();
    }

    @Override
    public void execute(String input) {
        controller.readYamlAndMoveFiles(input);
    }
}
