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


package org.slc.sli.api.jersey.exceptionhandlers;

import org.slc.sli.api.exceptions.URITranslationException;
import org.slc.sli.api.representation.ErrorResponse;
import org.slc.sli.api.service.EntityNotFoundException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Handler for URI translation errors (gives an HTTP 404 not found response)
 */
@Provider
@Component
public class URITranslationExceptionHandler implements ExceptionMapper<URITranslationException> {

    public Response toResponse(URITranslationException e) {
        String message = "URI translation failed";
        if (e.getMessage() != null) {
            message += ": " + e.getMessage();
        }
        Response.Status errorStatus = Response.Status.NOT_FOUND;
        return Response
                .status(errorStatus)
                .entity(new ErrorResponse(errorStatus.getStatusCode(), errorStatus.getReasonPhrase(),
                        message)).build();
    }
}
