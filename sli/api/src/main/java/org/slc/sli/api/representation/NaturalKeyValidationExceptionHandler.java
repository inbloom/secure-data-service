package org.slc.sli.api.representation;

import org.slc.sli.validation.NaturalKeyValidationException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper NaturalKeyValidationExceptions
 *
 * @author srupasinghe
 */
@Provider
@Component
public class NaturalKeyValidationExceptionHandler implements ExceptionMapper<NaturalKeyValidationException> {

    public Response toResponse(NaturalKeyValidationException e) {
        String exceptionMessage = "Natural Key Validation failed: " + e.getEntityType();
        return Response
                .status(Response.Status.CONFLICT)
                .entity(new ErrorResponse(Response.Status.CONFLICT.getStatusCode(), Response.Status.CONFLICT.getReasonPhrase(),
                        exceptionMessage)).build();
    }
}
