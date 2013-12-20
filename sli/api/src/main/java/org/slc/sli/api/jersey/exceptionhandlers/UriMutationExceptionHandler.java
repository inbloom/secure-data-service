package org.slc.sli.api.jersey.exceptionhandlers;

import org.slc.sli.api.exceptions.UriMutationException;
import org.slc.sli.api.representation.ErrorResponse;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Handler for UriMutationExceptions, which should result in an HTTP 404 not found
 */
@Provider
@Component
public class UriMutationExceptionHandler implements ExceptionMapper<UriMutationException> {

    public Response toResponse(UriMutationException e) {
        String message = "URI mutation error";
        if (e.getMessage() != null) {
            message += ": " + e.getMessage();
        }

        Response.Status errorStatus = Response.Status.NOT_FOUND;
        return Response
                .status(errorStatus)
                .entity(new ErrorResponse(errorStatus.getStatusCode(), errorStatus.getReasonPhrase(),
                        message)).build();
    }
}
