package org.slc.sli.api.resources;

import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.SLIPrincipal;
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
    
    private static final Logger LOG = LoggerFactory.getLogger(SessionDebugResource.class);
    
    @Autowired
    RoleRightAccess             roleAccessor;
    
    @Value("${sli.security.noSession.landing.url}")
    private String              realmPage;
    
    /**
     * Method processing HTTP GET requests, producing "application/json" MIME media
     * type.
     * 
     * @return SecurityContext that will be send back as a response of type "application/json".
     */
    @GET
    @Path("debug")
    public SecurityContext getSecurityContext() {
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
        } else {
            sessionDetails.put("authenticated", false);
            sessionDetails.put("redirect_user", realmPage);
        }
        
        SecurityUtil.sudoRun(new SecurityTask() {
            
            @Override
            public void execute() {
                sessionDetails.put("all_roles", roleAccessor.fetchAllRoles());
            }
        });
        
        return sessionDetails;
    }
    
    private boolean isAuthenticated(SecurityContext securityContext) {
        return !(securityContext == null || securityContext.getAuthentication() == null || securityContext.getAuthentication().getCredentials() == null || securityContext.getAuthentication().getCredentials().equals(""));
    }
}
