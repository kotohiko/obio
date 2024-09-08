package org.jacob.im.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author Kotohiko
 * @since 12:12 Aug 07, 2024
 */
public class IMCommonConsoleInputReader {
    public static BufferedReader consoleReader() {
        return new BufferedReader(new InputStreamReader(System.in));
    }
}