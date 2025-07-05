package org.jacob.obio.ifp.constants;

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

    private IFPConstants() {
    }
}
