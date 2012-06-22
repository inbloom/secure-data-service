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

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.slc.sli.api.resources.security.RealmRoleManagerResource;
import org.slc.sli.api.security.SecurityEventBuilder;

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

    @Override
    public Response toResponse(AccessDeniedException e) {
        Response.Status errorStatus = Response.Status.FORBIDDEN;
        warn("Access has been denied to user: {}", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        audit(securityEventBuilder.createSecurityEvent(RealmRoleManagerResource.class.getName(), uriInfo, "Access Denied!"));
        return Response.status(errorStatus).entity(new ErrorResponse(errorStatus.getStatusCode(), errorStatus.getReasonPhrase(), "Access DENIED: " + e.getMessage())).build();
    }
}
