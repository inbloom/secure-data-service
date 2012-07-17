package org.slc.sli.aspect;

import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.common.util.logging.LoggerCarrier;

public aspect LoggerCarrierAspect {

    declare parents : (org.slc.sli.ingestion.processors.* &&
            !java.lang.Enum+)  implements LoggerCarrier;

    private MongoTemplate template;

    public MongoTemplate getTemplate() {
        return template;
    }

    public void setTemplate(MongoTemplate template) {
        this.template = template;
    }

    public void LoggerCarrier.audit(SecurityEvent event) {
        LoggerCarrierAspect.aspectOf().getTemplate().save(event);
        
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
