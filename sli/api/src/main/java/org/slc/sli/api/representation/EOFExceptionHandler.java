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

import java.io.EOFException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;

/**
 * Exception handler to catch cases where an EOF exception is thrown. This can be the case if a user
 * tries to post a null body
 *
 * @author nbrown
 *
 */
@Provider
@Component
public class EOFExceptionHandler implements ExceptionMapper<EOFException> {

    @Override
    public Response toResponse(EOFException exception) {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

}
