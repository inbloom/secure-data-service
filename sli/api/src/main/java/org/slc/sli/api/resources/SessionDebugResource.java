package org.slc.sli.api.resources;

import org.slc.sli.api.security.enums.DefaultRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
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

    private static final Logger LOG = LoggerFactory.getLogger(SessionDebugResource.class);

    @Value("${security.noSession.landing.url}")
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
        return SecurityContextHolder.getContext();
    }

    @GET
    @Path("check")
    public Object sessionCheck(@Context final UriInfo uriInfo) {

        Map<String, Object> sessionDetails = new TreeMap<String, Object>();

        if (isAuthenticated(SecurityContextHolder.getContext())) {
            sessionDetails.put("authenticated", true);
            sessionDetails.put("sessionId", SecurityContextHolder.getContext().getAuthentication().getCredentials());
            sessionDetails.put("granted_authorities", SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        } else {
            sessionDetails.put("authenticated", false);
            sessionDetails.put("redirect_user", getLoginUrl(uriInfo));
        }

        sessionDetails.put("all_roles", DefaultRoles.getDefaultRoleNames());

        return sessionDetails;
    }

    private boolean isAuthenticated(SecurityContext securityContext) {
        return !(securityContext == null || securityContext.getAuthentication() == null || securityContext.getAuthentication().getCredentials() == null || securityContext.getAuthentication().getCredentials().equals(""));
    }

    private String getLoginUrl(UriInfo uriInfo) {
        return realmPage;
    }

}
