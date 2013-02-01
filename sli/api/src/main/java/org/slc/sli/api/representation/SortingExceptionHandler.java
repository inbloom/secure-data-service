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

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;

import org.slc.sli.api.service.query.SortingException;

/**
 * Exception handler for SortingExceptions
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@Provider
@Component
public class SortingExceptionHandler implements ExceptionMapper<SortingException> {
    
    @Override
    public Response toResponse(SortingException e) {
        Response.Status errorStatus = Response.Status.BAD_REQUEST;
        return Response.status(errorStatus)
                .entity(new ErrorResponse(errorStatus.getStatusCode(), errorStatus.getReasonPhrase(), e.getMessage()))
                .build();
    }
    
}
