package org.slc.sli.api.representation;

import org.slc.sli.api.selectors.model.SelectorParseException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author jstokes
 */
@Provider
@Component
public class SelectorParseExceptionHandler implements ExceptionMapper<SelectorParseException> {

    public Response toResponse(SelectorParseException e) {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(Status.BAD_REQUEST.getStatusCode(), Status.BAD_REQUEST.getReasonPhrase(),
                        e.getMessage())).build();
    }
}

