package org.jacob.im.obfo;

import org.jacob.im.common.helper.IMCommonHelper;
import org.jacob.im.obfo.controller.ReadAndMoveController;
import org.jacob.im.obfo.monitor.MonitorStarter;

/**
 * @author Kotohiko
 * @since 07:26 Aug 02, 2024
 */
public final class OneButtonApplication {
    public static void main(String[] args) {
        var addFileMonitor = MonitorStarter.monitorStarter();
        System.out.println(IMCommonHelper.getRealTime()
                + " INFO [Server] [Monitor service] " + addFileMonitor + " has started");
        ReadAndMoveController.mainPart();
    }
}