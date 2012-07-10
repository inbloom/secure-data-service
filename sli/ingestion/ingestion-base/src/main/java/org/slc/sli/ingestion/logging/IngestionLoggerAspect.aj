
package org.slc.sli.ingestion.logging;

import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.util.LogUtil;

public aspect IngestionLoggerAspect {

    declare parents : (org.slc.sli.ingestion..*
            && !org.slc.sli.ingestion.handler.AbstractIng*
            && !org.slc.sli.ingestion.handler.Handl*
            && !org.slc.sli.ingestion.transformation.AbstractTrans*
            && !org.slc.sli.ingestion.validation.ErrorRep*
            && !org.slc.sli.ingestion.validation.spring.SimpleV*
            && !java.lang.Enum+ ) implements IngestionLogger;

    public void IngestionLogger.debug(String msg) {
        LoggerFactory.getLogger(this.getClass()).debug(msg);
    }

    public void IngestionLogger.info(String msg) {
        LoggerFactory.getLogger(this.getClass()).info(msg);
    }

    public void IngestionLogger.warn(String msg) {
        LoggerFactory.getLogger(this.getClass()).warn(msg);
    }

    public void IngestionLogger.debug(String msg, Object... params) {
        LoggerFactory.getLogger(this.getClass()).debug(msg, params);
    }

    public void IngestionLogger.info(String msg, Object... params) {
        LoggerFactory.getLogger(this.getClass()).info(msg, params);
    }

    public void IngestionLogger.warn(String msg, Object... params) {
        LoggerFactory.getLogger(this.getClass()).warn(msg, params);
    }

    public void IngestionLogger.error(String msg, Object... params) {
        LoggerFactory.getLogger(this.getClass()).error(msg, params);
    }

    public void IngestionLogger.error(String msg, Throwable x) {
        LoggerFactory.getLogger(this.getClass()).error(msg, x);
    }

    public void IngestionLogger.piiClearedError(String msg, Throwable x) {
        LogUtil.error(LoggerFactory.getLogger(this.getClass()), msg, (Exception)x);
    }

}