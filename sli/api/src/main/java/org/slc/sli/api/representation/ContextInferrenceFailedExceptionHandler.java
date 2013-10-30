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

import java.util.Collections;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.annotation.XmlRootElement;

import org.slc.sli.api.security.pdp.ContextInferrenceFailedException;
import org.springframework.stereotype.Component;

/**
 * Handles empty contexts
 * 
 * @author dkornishev
 * 
 */
@Provider
@Component
public class ContextInferrenceFailedExceptionHandler implements ExceptionMapper<ContextInferrenceFailedException> {

    @Context
    private HttpHeaders headers;
    
    @Override
    public Response toResponse(ContextInferrenceFailedException exception) {
        warn("Failed Context Inferrence");
        Object entity = Collections.EMPTY_LIST;
        if (headers.getAcceptableMediaTypes().contains(MediaType.APPLICATION_XML_TYPE)) {
            entity = new EmptyResponse();
        }
        
        return Response.status(Status.NOT_FOUND).entity(entity).header("TotalCount", 0).build();
    }
    
    /**
     * Represents an empty document to be returned to the API user (no fields, no data).
     * 
     * 
     * @author kmyers
     *
     */
    @XmlRootElement(name = "emptyList")
    public static class EmptyResponse {
    }
}
