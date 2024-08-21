package org.jacob.im.obfo;

import org.jacob.im.obfo.constants.OBFOConstants;
import org.jacob.im.obfo.monitor.AddFileMonitor;
import org.jacob.im.obfo.service.ReadAndMoveService;

import java.nio.file.Paths;

/**
 * @author Jacob Suen
 * @since 07:26 Aug 02, 2024
 */
public final class OneButtonApplication {
    public static void main(String[] args) {
        new AddFileMonitor(Paths.get(OBFOConstants.UNCLASSIFIED_REMAINING_IMAGES_FOLDER_PATH), 5);
        ReadAndMoveService.serviceMainPart();
    }
}