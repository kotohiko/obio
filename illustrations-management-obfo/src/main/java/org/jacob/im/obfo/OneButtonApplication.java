package org.jacob.im.obfo;

import org.jacob.im.obfo.controller.ReadAndMoveController;
import org.jacob.im.obfo.monitor.MonitorStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kotohiko
 * @since 07:26 Aug 02, 2024
 */
public final class OneButtonApplication {

    private static final Logger logger = LoggerFactory.getLogger(OneButtonApplication.class);

    public static void main(String[] args) {
        var addFileMonitor = MonitorStarter.monitorStarter();
        logger.info("{} has started successfully.", addFileMonitor);
        ReadAndMoveController.mainPart();
    }
}