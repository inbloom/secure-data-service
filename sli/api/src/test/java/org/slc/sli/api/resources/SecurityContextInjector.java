package org.slc.sli.api.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.enums.Right;
import org.slc.sli.api.security.resolve.RolesToRightsResolver;
import org.slc.sli.api.security.roles.DefaultRoleRightAccessImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
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

        setPrincipalAndSecurityContext(user, fullName, DefaultRoleRightAccessImpl.IT_ADMINISTRATOR);

    }

    public void setResolver(RolesToRightsResolver resolver) {
        this.resolver = resolver;
    }

    private PreAuthenticatedAuthenticationToken getAuthenticationToken(String token, SLIPrincipal principal) {
        SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(null, null, Arrays.asList(Right.values())));
        Set<GrantedAuthority> grantedAuthorities = this.resolver.resolveRoles(principal.getRealm(), principal.getRoles());
        SecurityContextHolder.clearContext();

        PreAuthenticatedAuthenticationToken preAuthenticatedAuthenticationToken = new PreAuthenticatedAuthenticationToken(principal, token, grantedAuthorities);
        return preAuthenticatedAuthenticationToken;
    }

    public void setEducatorContext() {
        String user = "educator";
        String fullName = "Educator";
        setPrincipalAndSecurityContext(user, fullName, DefaultRoleRightAccessImpl.EDUCATOR);
    }

    private void setPrincipalAndSecurityContext(String user, String fullName, String role) {
        String token = "AQIC5wM2LY4SfczsoqTgHpfSEciO4J34Hc5ThvD0QaM2QUI.*AAJTSQACMDE.*";

        // setTheirRoles will require a list
        List<String> roles = new ArrayList<String>();
        roles.add(role);

        SLIPrincipal principal = buildPrincipal(user, fullName, roles);

        LOG.debug("assembling authentication token");
        PreAuthenticatedAuthenticationToken authenticationToken = getAuthenticationToken(token, principal);

        LOG.debug("updating security context for principal " + role);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private SLIPrincipal buildPrincipal(String user, String fullName, List<String> roles) {
        SLIPrincipal principal = new SLIPrincipal(user);
        principal.setId(user);
        principal.setName(fullName);
        principal.setRoles(roles);
        return principal;
    }

}
