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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;

/**
 * These are for status codes we want to use but aren't yet available until JAX-RS 2.0.
 *
 * If we upgrade to 2.0, we can get rid of this.
 */
public class CustomStatus {
    
    public static final Response.StatusType ENTITY_TOO_LARGE = buildStatusType(413, "Request Entity Too Large", Response.Status.Family.CLIENT_ERROR);
    public static final Response.StatusType FOUND = buildStatusType(302, "Found", Response.Status.Family.REDIRECTION);
    public static final Response.StatusType NOT_IMPLEMENTED = buildStatusType(501, "Not Implemented", Response.Status.Family.SERVER_ERROR);
    
    private static Response.StatusType buildStatusType(final int code, final String reasonPhrase, final Status.Family family) {
        return new Response.StatusType() {
            
            @Override
            public int getStatusCode() {
                return code;
            }
            
            @Override
            public String getReasonPhrase() {
                return reasonPhrase;
            }
            
            @Override
            public Family getFamily() {
                return family;
            }
        };
    }

}
