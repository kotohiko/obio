package org.jacob.im.obfo;

import org.jacob.im.obfo.monitor.MonitorStarter;
import org.jacob.im.obfo.service.ReadAndMoveService;

/**
 * @author Jacob Suen
 * @since 07:26 Aug 02, 2024
 */
public final class OneButtonApplication {
    public static void main(String[] args) {
        var addFileMonitor = MonitorStarter.monitorStarter();
        System.out.println("[Monitor service] " + addFileMonitor + " has started");
        ReadAndMoveService.serviceMainPart();
    }
}