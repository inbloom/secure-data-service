package org.slc.sli.api.representation;

import org.slc.sli.api.resources.generic.MethodNotAllowedException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper for dis allowed methods
 *
 * @author srupasinghe
 */

@Provider
@Component
public class MethodNotAllowedExceptionHandler implements ExceptionMapper<MethodNotAllowedException> {

    public Response toResponse(MethodNotAllowedException e) {
        String message = "Method Not Allowed [" + e.getAllowedMethods() + "]";

        Response.ResponseBuilder builder =  Response
                .status(405)
                .entity(new ErrorResponse(405, "Method Not Allowed",
                        message));

        builder.header("Allow", "Allow: " + e.getAllowedMethods());

        return builder.build();
    }
}
