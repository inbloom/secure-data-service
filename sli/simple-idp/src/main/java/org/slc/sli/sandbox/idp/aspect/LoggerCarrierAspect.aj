package org.slc.sli.sandbox.idp.aspect;

import java.lang.management.ManagementFactory;

import org.slc.sli.common.util.datetime.DateTimeUtil;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.common.util.logging.LoggerCarrier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

public aspect LoggerCarrierAspect {
    
    private static final Logger LOG = LoggerFactory.getLogger("SecurityMonitor");

    private static final String SIMPLEIDP_APPLICATION_ID = "SimpleIDP";
        
    private MongoTemplate template;

    declare parents : org.slc.sli.sandbox.idp.controller.Login implements LoggerCarrier;
      
    public void LoggerCarrier.audit(SecurityEvent event) {
        event.setAppId(SIMPLEIDP_APPLICATION_ID);
        event.setClassName(this.getClass().getName());
        event.setTimeStamp(DateTimeUtil.getNowInUTC());
        event.setProcessNameOrId(ManagementFactory.getRuntimeMXBean().getName());
        
        LoggerCarrierAspect.aspectOf().getTemplate().save(event);
        
        switch (event.getLogLevel()) {
        case TYPE_DEBUG:
            LOG.debug(event.toString());
            break;
        
        case TYPE_WARN:
            LOG.warn(event.toString());
            break;
        
        case TYPE_INFO:
            LOG.info(event.toString());
            break;
        
        case TYPE_ERROR:
            LOG.error(event.toString());
            break;
        
        case TYPE_TRACE:
            LOG.trace(event.toString());
            break;
        
        default:
            LOG.info(event.toString());
            break;
        }
    }

    public MongoTemplate getTemplate() {
        return template;
    }

    public void setTemplate(MongoTemplate template) {
        this.template = template;
    }

}
