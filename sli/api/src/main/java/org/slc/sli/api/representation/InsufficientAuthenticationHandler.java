/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.api.representation;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slc.sli.api.security.oauth.OAuthAccessException;
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
        String wwwAuthHeader = this.authUrl;
        
        //If we have an embedded OAuth exception, then put the error information in the www-auth header per oauth spec 
        //http://tools.ietf.org/html/rfc6750 see sec 3
        //Otherwise put the auth url in the header
        if (exception.getCause() != null && exception.getCause() instanceof OAuthAccessException) {
            OAuthAccessException oauthEx = (OAuthAccessException) exception.getCause();
            wwwAuthHeader = "Bearer error=\"" + oauthEx.getType().toString() + "\", error_description=\"" + oauthEx.getMessage() + "\"";
        }
        return Response.status(status).entity(new ErrorResponse(status.getStatusCode(), status.getReasonPhrase(), 
                "Access DENIED: " + exception.getMessage())).header(HttpHeaders.WWW_AUTHENTICATE, wwwAuthHeader).build();
    }

}
