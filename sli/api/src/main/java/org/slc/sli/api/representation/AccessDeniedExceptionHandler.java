/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slc.sli.api.resources.security.RealmResource;
import org.slc.sli.api.security.SecurityEventBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Handler for catching access denied exceptions.
 *
 * @author shalka
 */
@Provider
@Component
public class AccessDeniedExceptionHandler implements ExceptionMapper<AccessDeniedException> {

    @Autowired
    private SecurityEventBuilder securityEventBuilder;

    @Context
    UriInfo uriInfo;
    
    @Context
    private HttpHeaders headers;
    
    @Context
    private HttpServletResponse response;

    @Override
    public Response toResponse(AccessDeniedException e) {
        
        //There are a few jax-rs resources that generate HTML content, and we want the
        //default web-container error handler pages to get used in those cases.
        if (headers.getAcceptableMediaTypes().contains(MediaType.TEXT_HTML_TYPE)) {
            try {
                response.sendError(403, e.getMessage());
                return null;    //the error page handles the response, so no need to return a response
            } catch (IOException ex) {
                error("Error displaying error page", ex);
            }
        }
        
        Response.Status errorStatus = Response.Status.FORBIDDEN;
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            warn("Access has been denied to user: {}", SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal());
        } else {
            warn("Access has been denied to user for being incorrectly associated");
        }
        warn("Cause: {}", e.getMessage());
        audit(securityEventBuilder.createSecurityEvent(RealmResource.class.getName(), uriInfo.getRequestUri(), "Access Denied!"));
        return Response.status(errorStatus).entity(new ErrorResponse(errorStatus.getStatusCode(), errorStatus.getReasonPhrase(), "Access DENIED: " + e.getMessage())).build();
    }
}
