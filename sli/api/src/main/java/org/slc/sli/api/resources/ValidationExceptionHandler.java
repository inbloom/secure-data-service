package org.slc.sli.api.resources;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slc.sli.validation.EntityValidationException;

/**
 * Hander for validation errors
 */
@Provider
public class ValidationExceptionHandler implements ExceptionMapper<EntityValidationException> {

    public Response toResponse(EntityValidationException e) {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}
