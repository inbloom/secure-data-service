package org.slc.sli.api.jersey;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import org.slc.sli.api.security.SLIPrincipal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.OauthSessionManager;
import org.slc.sli.dal.TenantContext;

/**
 * Pre-request processing filter.  
 * Adds security information for the user
 * Records start time of the request
 * 
 * @author dkornishev
 * 
 */
@Component
public class PreProcessFilter implements ContainerRequestFilter {
        
    @Autowired
    private OauthSessionManager manager;
    
    @Override
    public ContainerRequest filter(ContainerRequest request) {
        recordStartTime(request);
        populateSecurityContext(request);
        return request;
    }

    private void populateSecurityContext(ContainerRequest request) {
        OAuth2Authentication auth = manager.getAuthentication(request.getHeaderValue("Authorization"));
        SecurityContextHolder.getContext().setAuthentication(auth);
        TenantContext.setTenantId(( (SLIPrincipal) auth.getPrincipal()).getTenantId());
    }

    private void recordStartTime(ContainerRequest request) {
        request.getProperties().put("startTime", System.currentTimeMillis());
    }
}
