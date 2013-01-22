package org.slc.sli.api.representation;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.stereotype.Component;

/**
 * Handle connection issues to mongo
 *
 * @author nbrown
 *
 */
@Provider
@Component
public class UncategorizedMongoExceptionHandler implements ExceptionMapper<UncategorizedMongoDbException> {

    @Override
    public Response toResponse(UncategorizedMongoDbException exception) {
        Status errorStatus = Status.SERVICE_UNAVAILABLE;
        return Response
                .status(errorStatus)
                .entity(new ErrorResponse(errorStatus.getStatusCode(), errorStatus.getReasonPhrase(),
                        "Could not access database")).build();
    }

}
