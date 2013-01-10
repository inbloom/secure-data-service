package org.slc.sli.sandbox.idp.aspect;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

import org.slc.sli.common.util.datetime.DateTimeUtil;
import org.slc.sli.common.util.logging.LoggerCarrier;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public aspect LoggerCarrierAspect {
    
    private static final Logger LOG = LoggerFactory.getLogger("SecurityMonitor");

    private static final String SIMPLEIDP_APPLICATION_ID = "SimpleIDP";
        
    declare parents : org.slc.sli.sandbox.idp.controller.Login implements LoggerCarrier;
      
    @Autowired
    private Repository<Entity> mongoEntityRepository;
    
    public void LoggerCarrier.audit(SecurityEvent event) {
        event.setAppId(SIMPLEIDP_APPLICATION_ID);
        event.setClassName(this.getClass().getName());
        event.setTimeStamp(DateTimeUtil.getNowInUTC());
        event.setProcessNameOrId(ManagementFactory.getRuntimeMXBean().getName());
        
        Map<String, Object> metadata = new HashMap<String, Object>();
        metadata.put("tenantId", event.getTenantId());        
        LoggerCarrierAspect.aspectOf().getRepo().create("securityEvent", event.getProperties(), metadata, "securityEvent");
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

    public Repository<Entity> getRepo() {
        return mongoEntityRepository;
    }

    public void setRepo(Repository<Entity> repo) {
        this.mongoEntityRepository = repo;
    }

}
