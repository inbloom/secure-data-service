package org.slc.sli.api.representation;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;

import org.slc.sli.api.service.EntityNotFoundException;

/**
 * Hander for validation errors
 */
@Provider
@Component
public class EntityNotFoundHandler implements ExceptionMapper<EntityNotFoundException> {

    public Response toResponse(EntityNotFoundException e) {
        Response.Status errorStatus = Response.Status.NOT_FOUND;
        return Response.status(errorStatus)
            .entity(new ErrorResponse(errorStatus.getStatusCode(), errorStatus.getReasonPhrase(),
                        "Entity not found: " + e.getId())).build();
    }
}
