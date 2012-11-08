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

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;

import org.slc.sli.api.service.ResourceNotFoundException;

/**
 * Hander for resource not found errors
 */
@Provider
@Component
public class ResourceNotFoundHandler implements ExceptionMapper<ResourceNotFoundException> {

    @Override
    public Response toResponse(ResourceNotFoundException e) {
        return Response
                .status(Status.NOT_FOUND)
                .entity(new ErrorResponse(Status.NOT_FOUND.getStatusCode(), Status.NOT_FOUND.getReasonPhrase(),
                        "Invalid resource path: " + e.getTypePath())).build();
    }
}
