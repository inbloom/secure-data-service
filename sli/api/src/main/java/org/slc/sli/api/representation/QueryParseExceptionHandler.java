package org.slc.sli.api.representation;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slc.sli.api.service.query.QueryParseException;
import org.springframework.stereotype.Component;

/**
 * Hander for Query Parsing errors
 */
@Provider
@Component
public class QueryParseExceptionHandler implements ExceptionMapper<QueryParseException> {
    
    public Response toResponse(QueryParseException e) {
        Response.Status errorStatus = Response.Status.NOT_FOUND;
        return Response
                .status(errorStatus)
                .entity(new ErrorResponse(errorStatus.getStatusCode(), errorStatus.getReasonPhrase(),
                        "Error Parsing the Query: " + e.getQueryString())).build();
    }
}
