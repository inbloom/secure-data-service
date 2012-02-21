package org.slc.sli.api.representation;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;

import org.slc.sli.api.service.query.SortingException;

/**
 * Exception handler for SortingExceptions
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@Provider
@Component
public class SortingExceptionHandler implements ExceptionMapper<SortingException> {
    
    @Override
    public Response toResponse(SortingException e) {
        Response.Status errorStatus = Response.Status.BAD_REQUEST;
        return Response.status(errorStatus)
                .entity(new ErrorResponse(errorStatus.getStatusCode(), errorStatus.getReasonPhrase(), e.getMessage()))
                .build();
    }
    
}
