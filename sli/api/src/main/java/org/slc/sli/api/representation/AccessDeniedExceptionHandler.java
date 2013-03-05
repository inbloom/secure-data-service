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
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.slc.sli.api.resources.security.RealmResource;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.SecurityEventBuilder;

/**
 * Handler for catching access denied exceptions.
 *
 * @author shalka
 */
@Provider
@Component
public class AccessDeniedExceptionHandler implements ExceptionMapper<AccessDeniedException> {

    public static final String ED_ORG_START = "<" ;
    public static final String ED_ORG_END = ">" ;
    public static final String NO_EDORG = "UNAVAILABLE";

    @Autowired
    private SecurityEventBuilder securityEventBuilder;

    @Context
    UriInfo uriInfo;

    @Context
    private HttpHeaders headers;

    @Context
    private HttpServletResponse response;

    /*
     *  Target EdOrgsIDs might be passed in to the exception as a part of the error message, enclosed in <>
     *  E.g.: Insufficient Rights <TargetEdOrgID>
     */

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
        SLIPrincipal principal = null ;
        String message = e.getMessage();
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            principal = (SLIPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            warn("Access has been denied to user: {}",principal );
        } else {
            warn("Access has been denied to user for being incorrectly associated");
        }
        warn("Cause: {}", message);
        String reason = message.indexOf( ED_ORG_START) > 0 ? message.substring( 0, message.indexOf( ED_ORG_START) ) : message;

        audit(securityEventBuilder.createSecurityEvent(RealmResource.class.getName(), uriInfo.getRequestUri(), "Access Denied:"
                    + reason,  getTargetEdOrgs( message )));
        return Response.status(errorStatus).entity(new ErrorResponse(errorStatus.getStatusCode(), errorStatus.getReasonPhrase(), "Access DENIED: " + message)).build();
    }

  // Trying to see if EntityId of the entity, user tried to access, was passed as a part of the message
    private ArrayList<String> getTargetEdOrgs( String message ) {
        if( message == null) {
            return( null );
        }
        int start = message.indexOf(ED_ORG_START);
        int end = message.indexOf(ED_ORG_END);

        String target = null;
        if( start >= 0 && end >= 0 && end > start ) {
           target = message.substring(start + 1, end).trim() ;
        } else {
            warn( "Could not extract target EdOrgs from " + message );
            return( null);
        }
        ArrayList<String> targetEdOrgs = null;
        if( target != null ) {
            targetEdOrgs = fromString( target );
        }

     return( targetEdOrgs )  ;
    }

    //Trying to convert back string that was composed by Array.toString()
    private ArrayList<String> fromString( String edOrgs) {
        String[] target = edOrgs.replace( "[","").replace("]","").split(", ");

        return new ArrayList<String>(Arrays.asList( target));

    }

}
