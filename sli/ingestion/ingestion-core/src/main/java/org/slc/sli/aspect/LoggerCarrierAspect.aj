package org.slc.sli.aspect;

import org.slf4j.LoggerFactory;
import org.slc.sli.common.util.logging.SecurityEvent;

public aspect LoggerCarrierAspect {

    declare parents : (org.slc.sli.ingestion.processors.* &&
            !java.lang.Enum+)  implements LoggerCarrier;

    public void LoggerCarrier.audit(SecurityEvent event) {
        switch (event.getLogLevel()) {
            case TYPE_DEBUG:
                LoggerFactory.getLogger("SecurityMonitor").debug(event.toString());
                break;

            case TYPE_WARN:
                LoggerFactory.getLogger("SecurityMonitor").warn(event.toString());
                break;

            case TYPE_INFO:
                LoggerFactory.getLogger("SecurityMonitor").info(event.toString());
                break;

            case TYPE_ERROR:
                LoggerFactory.getLogger("SecurityMonitor").error(event.toString());
                break;

            case TYPE_TRACE:
                LoggerFactory.getLogger("SecurityMonitor").trace(event.toString());
                break;

            default:
                LoggerFactory.getLogger("SecurityMonitor").info(event.toString());
                break;
        }
    }
}
