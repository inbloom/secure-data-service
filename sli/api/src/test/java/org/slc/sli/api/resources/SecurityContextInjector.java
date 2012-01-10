package org.slc.sli.api.resources;

import java.util.ArrayList;
import java.util.List;

import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.resolve.RolesToRightsResolver;
import org.slc.sli.api.security.resolve.impl.DefaultClientRoleResolver;
import org.slc.sli.api.security.resolve.impl.DefaultRolesToRightsResolver;
import org.slc.sli.api.security.roles.DefaultRoleRightAccessImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Simple class for injecting a security context for unit tests. Future implementations will allow for greater
 * flexibility in selecting which roles become available to the user (including multiple roles).
 * 
 * @author shalka
 */

@Component
public class SecurityContextInjector {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityContextInjector.class);
    
    @Autowired
    private RolesToRightsResolver resolver;

    public void setAdminContext() {
        String user = "administrator";
        String fullName = "IT Administrator";
        String token = "AQIC5wM2LY4SfczsoqTgHpfSEciO4J34Hc5ThvD0QaM2QUI.*AAJTSQACMDE.*";
        
        // setTheirRoles will require a list
        List<String> roles = new ArrayList<String>();
        roles.add(DefaultRoleRightAccessImpl.IT_ADMINISTRATOR);
        
        SLIPrincipal principal = new SLIPrincipal();
        principal.setId(user);
        principal.setName(fullName);
        principal.setRoles(roles);
        
        LOG.debug("assembling authentication token");
        PreAuthenticatedAuthenticationToken authenticationToken = new PreAuthenticatedAuthenticationToken(principal,
                token, resolver.resolveRoles(roles));
        
        LOG.debug("updating security context for principal (IT Administrator)");
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    public void setEducatorContext() {
        String user = "educator";
        String fullName = "Educator";
        String token = "AQIC5wM2LY4SfczsoqTgHpfSEciO4J34Hc5ThvD0QaM2QUI.*AAJTSQACMDE.*";
        
        // setTheirRoles will require a list
        List<String> roles = new ArrayList<String>();
        roles.add(DefaultRoleRightAccessImpl.EDUCATOR);

        SLIPrincipal principal = new SLIPrincipal();
        principal.setId(user);
        principal.setName(fullName);
        principal.setRoles(roles);
        
        LOG.debug("assembling authentication token");
        PreAuthenticatedAuthenticationToken authenticationToken = new PreAuthenticatedAuthenticationToken(principal,
                token, resolver.resolveRoles(roles));
        
        LOG.debug("updating security context for principal (Educator)");
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private DefaultRolesToRightsResolver getRightsResolver() {
        DefaultRolesToRightsResolver resolver = new DefaultRolesToRightsResolver();
        resolver.setRoleMapper(new DefaultClientRoleResolver());
        return resolver;
    }
}
