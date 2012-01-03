package org.slc.sli.api.resources;

import java.util.ArrayList;
import java.util.List;

import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.roles.DefaultRoleMapperImpl;
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
        roles.add("IT Administrator");
        
        SLIPrincipal principal = new SLIPrincipal();
        principal.setId(user);
        principal.setName(fullName);
        principal.setTheirRoles(roles);
        
        LOG.debug("assembling authentication token");
        PreAuthenticatedAuthenticationToken authenticationToken = new PreAuthenticatedAuthenticationToken(principal,
                token, new DefaultRoleMapperImpl(principal.getTheirRoles()).buildMappedRoles());
        
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
        roles.add("Educator");
        
        SLIPrincipal principal = new SLIPrincipal();
        principal.setId(user);
        principal.setName(fullName);
        principal.setTheirRoles(roles);
        
        LOG.debug("assembling authentication token");
        PreAuthenticatedAuthenticationToken authenticationToken = new PreAuthenticatedAuthenticationToken(principal,
                token, new DefaultRoleMapperImpl(principal.getTheirRoles()).buildMappedRoles());
        
        LOG.debug("updating security context for principal (Educator)");
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
