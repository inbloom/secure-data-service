package org.slc.sli.api.representation;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Handler for catching access denied exceptions.
 * @author shalka
 */
@Provider
@Component
public class AccessDeniedExceptionHandler implements ExceptionMapper<AccessDeniedException> {
    public Response toResponse(AccessDeniedException e) {
        Response.Status errorStatus = Response.Status.FORBIDDEN;
        return Response.status(errorStatus)
                .entity(new ErrorResponse(errorStatus.getStatusCode(), errorStatus.getReasonPhrase(),
                        "Access DENIED: " + e.getMessage())).build();
    }
}
