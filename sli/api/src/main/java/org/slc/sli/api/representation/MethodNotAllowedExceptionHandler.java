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

import org.slc.sli.api.resources.generic.MethodNotAllowedException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper for dis allowed methods
 *
 * @author srupasinghe
 */

@Provider
@Component
public class MethodNotAllowedExceptionHandler implements ExceptionMapper<MethodNotAllowedException> {

    public Response toResponse(MethodNotAllowedException e) {
        String message = "Method Not Allowed [" + e.getAllowedMethods() + "]";

        Response.ResponseBuilder builder =  Response
                .status(405)
                .entity(new ErrorResponse(405, "Method Not Allowed",
                        message));

        builder.header("Allow", "Allow: " + e.getAllowedMethods());

        return builder.build();
    }
}
