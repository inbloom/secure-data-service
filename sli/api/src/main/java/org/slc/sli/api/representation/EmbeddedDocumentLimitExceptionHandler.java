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

import org.slc.sli.api.selectors.doc.EmbeddedDocumentLimitException;
import org.springframework.stereotype.Component;

/**
 * @author jstokes
 */
@Provider
@Component
public class EmbeddedDocumentLimitExceptionHandler implements ExceptionMapper<EmbeddedDocumentLimitException> {

    @Override
    public Response toResponse(EmbeddedDocumentLimitException e) {
        return Response
                .status(CustomStatus.ENTITY_TOO_LARGE)
                .entity(new ErrorResponse(CustomStatus.ENTITY_TOO_LARGE.getStatusCode(), CustomStatus.ENTITY_TOO_LARGE.getReasonPhrase(),
                        e.getMessage())).build();
    }
}

