package org.slc.sli.api.representation;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Component;

/**
 * Translates InsufficientAuthenticationException to 401
 *
 * @author dkornishev
 *
 */
@Component
@Provider
public class InsufficientAuthenticationHandler implements ExceptionMapper<InsufficientAuthenticationException> {

    @Value("${sli.security.noSession.landing.url}")
    private String authUrl;

    @Override
    public Response toResponse(InsufficientAuthenticationException exception) {
        Status status = Response.Status.UNAUTHORIZED;

        return Response.status(status).entity(new ErrorResponse(status.getStatusCode(), status.getReasonPhrase(), "Access DENIED: " + exception.getMessage())).header(HttpHeaders.WWW_AUTHENTICATE, this.authUrl).build();
    }

}
