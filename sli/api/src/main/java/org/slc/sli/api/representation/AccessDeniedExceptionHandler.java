package org.slc.sli.api.representation;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.slc.sli.api.resources.security.RealmRoleManagerResource;
import org.slc.sli.api.security.SecurityEventBuilder;

/**
 * Handler for catching access denied exceptions.
 *
 * @author shalka
 */
@Provider
@Component
public class AccessDeniedExceptionHandler implements ExceptionMapper<AccessDeniedException> {

    private static final Logger LOG = LoggerFactory.getLogger(GenericExceptionHandler.class);

    @Autowired
    private SecurityEventBuilder securityEventBuilder;

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(AccessDeniedException e) {
        Response.Status errorStatus = Response.Status.FORBIDDEN;
        LOG.warn("Access has been denied to user: {}", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        audit(securityEventBuilder.createSecurityEvent(RealmRoleManagerResource.class.getName(), uriInfo, "Access Denied!"));
        return Response.status(errorStatus).entity(new ErrorResponse(errorStatus.getStatusCode(), errorStatus.getReasonPhrase(), "Access DENIED: " + e.getMessage())).build();
    }
}
