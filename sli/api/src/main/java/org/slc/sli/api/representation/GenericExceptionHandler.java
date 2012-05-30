package org.slc.sli.api.representation;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;

/**
 * Hander for uncaught errors
 */
@Provider
@Component
public class GenericExceptionHandler implements ExceptionMapper<Throwable> {
    
    @Override
    public Response toResponse(Throwable e) {
        Response.Status errorStatus = Response.Status.INTERNAL_SERVER_ERROR;
        
        error("Caught exception thrown by ReST handler", e);
        
        return Response
                .status(errorStatus)
                .entity(new ErrorResponse(errorStatus.getStatusCode(), errorStatus.getReasonPhrase(),
                        "Internal Server Error: " + e.getMessage())).build();
    }
}
