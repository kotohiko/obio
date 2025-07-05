package org.jacob.obfo.core.command;

public interface UserCmd {

    boolean matches(String input);

    void execute(String input);

}
