package org.slc.sli.aspect;

import org.slf4j.LoggerFactory;
import org.slc.sli.common.util.logging.SecurityEvent;


public aspect LoggerCarrierAspect {

    declare parents : (org.slc.sli.api..* && !java.lang.Enum+ && !org.slc.sli.api.util.SecurityUtil.SecurityTask+)  implements LoggerCarrier;

    public void LoggerCarrier.debug(String msg) {
        LoggerFactory.getLogger(this.getClass()).debug(msg);
    }

    public void LoggerCarrier.info(String msg) {
        LoggerFactory.getLogger(this.getClass()).info(msg);
    }

    public void LoggerCarrier.warn(String msg) {
        LoggerFactory.getLogger(this.getClass()).warn(msg);
    }

    public void LoggerCarrier.debug(String msg, Object... params) {
        LoggerFactory.getLogger(this.getClass()).debug(msg, params);
    }

    public void LoggerCarrier.info(String msg, Object... params) {
        LoggerFactory.getLogger(this.getClass()).info(msg, params);
    }

    public void LoggerCarrier.warn(String msg, Object... params) {
        LoggerFactory.getLogger(this.getClass()).warn(msg, params);
    }
    
    public void LoggerCarrier.error(String msg, Object... params) {
        LoggerFactory.getLogger(this.getClass()).error(msg, params);        
    }

    public void LoggerCarrier.error(String msg, Throwable x) {
        LoggerFactory.getLogger(this.getClass()).error(msg, x);
    }

    public void LoggerCarrier.audit(SecurityEvent event) {
        LoggerFactory.getLogger("audit").info(event.toString());
    }
}
