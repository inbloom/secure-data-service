package org.slc.sli.api.resources;

import java.util.ArrayList;
import java.util.List;

import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.enums.DefaultRoles;
import org.slc.sli.api.security.resolve.impl.DefaultClientRoleResolver;
import org.slc.sli.api.security.resolve.impl.DefaultRolesToRightsResolver;
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
public class SecurityContextInjection {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityContextInjection.class);
    
    @Autowired
    public static void setAdminContext() {
        String user = "administrator";
        String fullName = "IT Administrator";
        String token = "AQIC5wM2LY4SfczsoqTgHpfSEciO4J34Hc5ThvD0QaM2QUI.*AAJTSQACMDE.*";
        
        // setTheirRoles will require a list
        List<String> roles = new ArrayList<String>();
        roles.add(DefaultRoles.ADMINISTRATOR.getRoleName());
        
        SLIPrincipal principal = new SLIPrincipal(user);
        principal.setName(fullName);
        principal.setRoles(roles);
        
        LOG.debug("assembling authentication token");
        PreAuthenticatedAuthenticationToken authenticationToken = new PreAuthenticatedAuthenticationToken(principal,
                token, getRightsResolver().resolveRoles(roles));
        
        LOG.debug("updating security context for principal (IT Administrator)");
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
    
    @Autowired
    public static void setEducatorContext() {
        String user = "educator";
        String fullName = "Educator";
        String token = "AQIC5wM2LY4SfczsoqTgHpfSEciO4J34Hc5ThvD0QaM2QUI.*AAJTSQACMDE.*";
        
        // setTheirRoles will require a list
        List<String> roles = new ArrayList<String>();
        roles.add(DefaultRoles.EDUCATOR.getRoleName());
        
        SLIPrincipal principal = new SLIPrincipal(user);
        principal.setName(fullName);
        principal.setRoles(roles);
        
        LOG.debug("assembling authentication token");
        PreAuthenticatedAuthenticationToken authenticationToken = new PreAuthenticatedAuthenticationToken(principal,
                token, getRightsResolver().resolveRoles(roles));
        
        LOG.debug("updating security context for principal (Educator)");
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    public static DefaultRolesToRightsResolver getRightsResolver() {
        DefaultRolesToRightsResolver resolver = new DefaultRolesToRightsResolver();
        resolver.setRoleMapper(new DefaultClientRoleResolver());
        return resolver;
    }
}
