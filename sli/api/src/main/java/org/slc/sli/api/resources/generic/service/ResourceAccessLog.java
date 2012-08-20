package org.slc.sli.api.resources.generic.service;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.security.SecurityEventBuilder;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriInfo;

/**
 * Log security events
 *
 */
@Component
public class ResourceAccessLog {

    @Autowired
    private EntityDefinitionStore entityDefinitionStore;

    @Autowired
    private SecurityEventBuilder securityEventBuilder;

    public void logAccessToRestrictedEntity(final UriInfo uriInfo, final Resource resource, final String loggingClass) {
        EntityDefinition definition = entityDefinitionStore.lookupByResourceName(resource.getResourceType());

        if (definition.isRestrictedForLogging()) {
            if (securityEventBuilder != null) {
                SecurityEvent event = securityEventBuilder.createSecurityEvent(loggingClass,
                        uriInfo, "restricted entity \"" + definition.getResourceName() + "\" is accessed.");
                audit(event);
            } else {
                warn("Cannot create security event, when restricted entity \"" + definition.getResourceName()
                        + "\" is accessed.");
            }
        }
    }
}
