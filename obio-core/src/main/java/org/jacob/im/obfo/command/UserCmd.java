package org.jacob.im.obfo.command;

public interface UserCmd {

    boolean matches(String input);

    void execute(String input);

}
