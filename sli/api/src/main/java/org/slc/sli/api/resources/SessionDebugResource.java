package org.slc.sli.api.resources;

import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Map;
import java.util.TreeMap;

/**
 * System resource class for security session context.
 * Hosted at the URI path "/system/session"
 */
@Path("/system/session")
@Component
@Scope("request")
@Produces("application/json")
public class SessionDebugResource {

    private String authUrl;
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
        
        Map<String,Object> sessionDetails = new TreeMap<String, Object>();

        if (isAuthenticated(SecurityContextHolder.getContext())) {
            sessionDetails.put("authenticated","yes");
            sessionDetails.put("sessionId", SecurityContextHolder.getContext().getAuthentication().getCredentials() );
        } else {
            sessionDetails.put("authenticated","no");
            sessionDetails.put("redirect_user",authUrl);
        }

        return sessionDetails;
    }

    private boolean isAuthenticated(SecurityContext securityContext) {
        return !(securityContext == null || securityContext.getAuthentication() == null
                || securityContext.getAuthentication().getCredentials() == null
                || securityContext.getAuthentication().getCredentials().equals(""));
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }


}
