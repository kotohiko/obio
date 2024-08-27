package org.jacob.im.ifp;

import org.jacob.im.ifp.controller.IFPEntranceController;

/**
 * Main class that launches the application.
 *
 * @author Jacob Suen
 * @since Oct 25, 2023
 */
public final class IFPApplication {
    public static void main(String[] args) {
        System.out.println("*************************************************   Welcome to Illustrations Filename Parser"
                + "   *************************************************");
        IFPEntranceController.getFilename();
    }
}
