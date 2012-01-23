package org.slc.sli.api.representation;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import org.slc.sli.validation.EntityValidationException;

/**
 * Hander for validation errors
 */
@Provider
@Component
public class ValidationExceptionHandler implements ExceptionMapper<EntityValidationException> {
    
    public Response toResponse(EntityValidationException e) {
        String exceptionMessage = "Validation failed: " + StringUtils.join(e.getValidationErrors(), "\n");
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(Status.BAD_REQUEST.getStatusCode(), Status.BAD_REQUEST.getReasonPhrase(),
                        exceptionMessage)).build();
    }
}
