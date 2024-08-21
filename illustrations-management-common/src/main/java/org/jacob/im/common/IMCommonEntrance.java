package org.jacob.im.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author Jacob Suen
 * @since 12:12 Aug 07, 2024
 */
public class IMCommonEntrance {
    public static BufferedReader imCommonEntrance() {
        return new BufferedReader(new InputStreamReader(System.in));
    }
}