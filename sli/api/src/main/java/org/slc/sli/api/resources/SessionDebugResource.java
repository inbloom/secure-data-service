package org.slc.sli.api.resources;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.resolve.ClientRoleResolver;
import org.slc.sli.api.security.roles.Role;
import org.slc.sli.api.security.roles.RoleRightAccess;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;

/**
 * System resource class for security session context.
 * Hosted at the URI path "/system/session"
 */
@Path("/system/session")
@Component
@Scope("request")
@Produces("application/json")
public class SessionDebugResource {
    
    @Autowired
    private RoleRightAccess roleAccessor;
    
    @Autowired
    private ClientRoleResolver roleResolver;
    
    @Value("${sli.security.noSession.landing.url}")
    private String realmPage;
    
    /**
     * Method processing HTTP GET requests, producing "application/json" MIME media
     * type.
     * 
     * @return SecurityContext that will be send back as a response of type "application/json".
     */
    @GET
    @Path("debug")
    public SecurityContext getSecurityContext() {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null) {
            throw new InsufficientAuthenticationException("User must be logged in");
        } else if (auth instanceof OAuth2Authentication) {
            if (((OAuth2Authentication) auth).getUserAuthentication() instanceof AnonymousAuthenticationToken) {
                throw new InsufficientAuthenticationException("User must be logged in");
            }
        } else if (auth instanceof AnonymousAuthenticationToken) {
            throw new InsufficientAuthenticationException("User must be logged in");
        }
        
        return SecurityContextHolder.getContext();
    }
    
    @GET
    @Path("check")
    public Object sessionCheck() {
        
        final Map<String, Object> sessionDetails = new TreeMap<String, Object>();
        
        if (isAuthenticated(SecurityContextHolder.getContext())) {
            sessionDetails.put("authenticated", true);
            sessionDetails.put("sessionId", SecurityContextHolder.getContext().getAuthentication().getCredentials());
            
            SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            sessionDetails.put("user_id", principal.getId());
            sessionDetails.put("full_name", principal.getName());
            sessionDetails.put("granted_authorities", principal.getRoles());
            sessionDetails.put("realm", principal.getRealm());
            sessionDetails.put("adminRealm", principal.getAdminRealm());
            sessionDetails.put("sliRoles", roleResolver.resolveRoles(principal.getRealm(), principal.getRoles()));
            
            List<Role> allRoles = SecurityUtil.sudoRun(new SecurityTask<List<Role>>() {
                @Override
                public List<Role> execute() {
                    return roleAccessor.fetchAllRoles();
                }
            });
            
            sessionDetails.put("all_roles", allRoles);
            
        } else {
            sessionDetails.put("authenticated", false);
            sessionDetails.put("redirect_user", realmPage);
        }
        
        return sessionDetails;
    }
    
    private boolean isAuthenticated(SecurityContext securityContext) {
        return !(securityContext == null || securityContext.getAuthentication() == null || securityContext.getAuthentication().getCredentials() == null || securityContext.getAuthentication().getCredentials().equals(""));
    }
}
