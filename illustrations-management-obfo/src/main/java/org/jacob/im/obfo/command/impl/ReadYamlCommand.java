package org.jacob.im.obfo.command.impl;

import org.jacob.im.obfo.command.Command;
import org.jacob.im.obfo.controller.ReadAndMoveController;

/**
 * Handles YAML processing commands.
 */
public class ReadYamlCommand implements Command {

    private final ReadAndMoveController controller;

    public ReadYamlCommand(ReadAndMoveController controller) {
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
