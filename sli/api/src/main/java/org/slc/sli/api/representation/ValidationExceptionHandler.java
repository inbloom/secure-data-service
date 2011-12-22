package org.slc.sli.api.representation;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;

import org.slc.sli.validation.EntityValidationException;

/**
 * Hander for validation errors
 */
@Provider
@Component
public class ValidationExceptionHandler implements ExceptionMapper<EntityValidationException> {

    public Response toResponse(EntityValidationException e) {
        return Response.status(Response.Status.BAD_REQUEST).entity(
                new ErrorResponse(Status.BAD_REQUEST.getStatusCode(), Status.BAD_REQUEST.getReasonPhrase(),
                    "Validation failed: " + e)).build();
    }
}
