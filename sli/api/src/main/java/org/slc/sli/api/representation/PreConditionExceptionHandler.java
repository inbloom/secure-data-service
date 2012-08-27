package org.slc.sli.api.representation;

import org.slc.sli.api.resources.generic.PreConditionFailedException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper for precondition failures
 *
 * @author srupasinghe
 */

@Provider
@Component
public class PreConditionExceptionHandler implements ExceptionMapper<PreConditionFailedException> {

    public Response toResponse(PreConditionFailedException e) {

        return Response
                .status(Response.Status.PRECONDITION_FAILED)
                .entity(new ErrorResponse(Response.Status.PRECONDITION_FAILED.getStatusCode(), Response.Status.PRECONDITION_FAILED.getReasonPhrase(),
                        e.getMessage())).build();

    }
}
