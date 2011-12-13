package org.slc.sli.api.resources;

import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * System resource class for security session context.
 * Hosted at the URI path "/system/session"
 */
@Path("/system/session")
@Component
@Scope("request")
public class SessionDebugResource {
    
    /**
     * Method processing HTTP GET requests, producing "application/json" MIME media
     * type.
     * 
     * @return SecurityContext that will be send back as a response of type "application/json".
     */
    @GET
    @Produces("application/json")
    public SecurityContext getSecurityContext() {
        return SecurityContextHolder.getContext();
    }
}
