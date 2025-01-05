package org.jacob.im.obfo.command;

public interface Command {

    boolean matches(String input);

    void execute(String input);

}
