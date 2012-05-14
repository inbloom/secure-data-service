package org.slc.sli.api.resources.security;

import org.slc.sli.domain.Entity;

public interface TenantResource {
    
    Entity createLandingZone(String tenantId, String edOrgId);
    
}