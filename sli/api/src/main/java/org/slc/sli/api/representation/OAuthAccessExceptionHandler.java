package org.slc.sli.api.representation;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import org.slc.sli.api.security.oauth.OAuthAccessException;
import org.slc.sli.api.security.oauth.OAuthAccessException.OAuthError;

/**
 *
 * Handle oauth access exceptions as described in 5.2
 */
@Component
@Provider
@Repository
public class OAuthAccessExceptionHandler implements ExceptionMapper<OAuthAccessException>  {


    @Override
    public Response toResponse(OAuthAccessException ex) {

        Response.Status errorStatus = null;

        if (ex.getType() == OAuthError.UNAUTHORIZED_CLIENT) {
            errorStatus = Response.Status.FORBIDDEN;
        } else {
            errorStatus = Response.Status.BAD_REQUEST;
        }

        Map data = new HashMap();
        data.put("error", ex.getType().toString());
        data.put("error_description", ex.getMessage());
        if (ex.getState() != null) {
            data.put("state", ex.getState());
        }
        return Response.status(errorStatus).entity(data).build();

    }

}
