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

import org.slc.sli.api.resources.generic.PreConditionFailedException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper for precondition failures
 *
 * @author srupasinghe
 */

@Provider
@Component
public class PreConditionExceptionHandler implements ExceptionMapper<PreConditionFailedException> {

    public Response toResponse(PreConditionFailedException e) {

        return Response
                .status(Response.Status.PRECONDITION_FAILED)
                .entity(new ErrorResponse(Response.Status.PRECONDITION_FAILED.getStatusCode(), Response.Status.PRECONDITION_FAILED.getReasonPhrase(),
                        e.getMessage())).build();

    }
}
