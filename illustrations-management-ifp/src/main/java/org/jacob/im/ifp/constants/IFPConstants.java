package org.jacob.im.ifp.constants;

/**
 * IFP constants class.
 *
 * @author Kotohiko
 * @since 12:04 Aug 21, 2024
 */
public class IFPConstants {

    /**
     * 9-digit PID regular expression.
     */
    public static final String PIXIV_PATTERN = "^\\d{9}_p\\d{1,2}$";

    /**
     * 8-digit PID regular expression.
     */
    public static final String PIXIV_PATTERN_2 = "^\\d{8}_p\\d{1,2}$";

    /**
     * IFP welcome message.
     */
    public static final String WELCOME_MESSAGE = """
            ******************************************************************************************************************
            **********************************   Welcome to Illustrations Filename Parser   **********************************
            ******************************************************************************************************************""";

    private IFPConstants() {
    }
}
