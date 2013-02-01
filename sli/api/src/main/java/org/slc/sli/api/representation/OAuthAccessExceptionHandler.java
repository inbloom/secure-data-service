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

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import org.slc.sli.api.security.oauth.OAuthAccessException;
import org.slc.sli.api.security.oauth.OAuthAccessException.OAuthError;

/**
 *
 * Handle oauth access exceptions as described in 5.2
 */
@Component
@Provider
@Repository
public class OAuthAccessExceptionHandler implements ExceptionMapper<OAuthAccessException>  {


    @SuppressWarnings("unchecked")
    @Override
    public Response toResponse(OAuthAccessException ex) {

        Response.Status errorStatus = null;

        if (ex.getType() == OAuthError.UNAUTHORIZED_CLIENT) {
            errorStatus = Response.Status.FORBIDDEN;
        } else {
            errorStatus = Response.Status.BAD_REQUEST;
        }

        @SuppressWarnings("rawtypes")
        Map data = new HashMap();
        data.put("error", ex.getType().toString());
        data.put("error_description", ex.getMessage());
        if (ex.getState() != null) {
            data.put("state", ex.getState());
        }
        return Response.status(errorStatus).entity(data).build();

    }

}
