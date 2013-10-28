package org.slc.sli.api.security.service;

import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Audit logging service.
 *
 * This replaces what was once implemented in the LoggerCarrierAspect.
 */
@Component
public class AuditLoggerImpl implements AuditLogger {

    private static final String LOG_CATEGORY = "audit";
    private static final Logger LOG = LoggerFactory.getLogger(LOG_CATEGORY);

    @Autowired
    private Repository<Entity> mongoEntityRepository;


    @Override
    public void auditLog(SecurityEvent event) {
        LOG.info(event.toString());
    }


    @Override
    public void audit(SecurityEvent event) {
        LOG.info(event.toString());

        if(mongoEntityRepository != null) {
            Map<String, Object> metadata = new HashMap<String, Object>();
            metadata.put("tenantId", event.getTenantId());
            mongoEntityRepository.create("securityEvent", event.getProperties(), metadata, "securityEvent");
        } else {
            LOG.error("[Could not log SecurityEvent to mongo!]");
        }
    }
}
