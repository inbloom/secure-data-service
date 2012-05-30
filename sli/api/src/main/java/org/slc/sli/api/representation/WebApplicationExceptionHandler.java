package org.slc.sli.api.representation;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;

/**
 * Hander for jax-rs web application exceptions
 */
@Provider
@Component
public class WebApplicationExceptionHandler implements ExceptionMapper<WebApplicationException> {
    
    @Override
    public Response toResponse(WebApplicationException e) {
        if (e.getResponse().getStatus() == 500) {
            error("Caught exception thrown by ReST handler", e);
            Response.Status errorStatus = Response.Status.INTERNAL_SERVER_ERROR;
            
            return Response
                    .status(errorStatus)
                    .entity(new ErrorResponse(errorStatus.getStatusCode(), errorStatus.getReasonPhrase(),
                            "Internal Server Error: " + e.getMessage())).build();
        }
        return e.getResponse();
    }
}
