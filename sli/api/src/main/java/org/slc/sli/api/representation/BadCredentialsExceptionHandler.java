package org.slc.sli.api.representation;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.BadCredentialsException;;

/*
 * Handler for catching bad credentials exceptions.
 */

@Provider
@Component
public class BadCredentialsExceptionHandler implements ExceptionMapper<BadCredentialsException> {   // map bad credentials exception to this handler
    public Response toResponse(BadCredentialsException e) {                                         // response from bad credentials exception
        Response.Status errorStatus = Response.Status.FORBIDDEN;                                    // error status is HTTP 403 (FORBIDDEN)
        return Response.status(errorStatus)                                                         // return the FORBIDDEN error status
            .entity(new ErrorResponse(errorStatus.getStatusCode(), errorStatus.getReasonPhrase(),   //  with the appropriate error response
                        "Access DENIED: " + e.getMessage())).build();                               //  and      appropriate error message
    }
}
