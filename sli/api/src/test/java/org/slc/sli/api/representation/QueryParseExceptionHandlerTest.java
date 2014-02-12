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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slc.sli.domain.QueryParseException;

import javax.ws.rs.core.Response;

/**
 * Tests that the error responses returned by neutral query failures do not contain expose the
 * query to the user (security implications).
 * 
 * @author kmyers
 *
 */
public class QueryParseExceptionHandlerTest {
    
    private QueryParseExceptionHandler queryParseExceptionHandler = new QueryParseExceptionHandler();
    
    /**
     * Make sure security injected IDs/limitations are not returned to the user in an error message.
     * 
     */
    @Test
    public void testNeutralQueryNotInResponse() {
        String message = "errorMessageForUser";
        String queryString = "systemQueryStringNotForUser";
        
        QueryParseException queryParseException = new QueryParseException(message, queryString);
        Response response = this.queryParseExceptionHandler.toResponse(queryParseException);
        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        assertTrue(errorResponse.getMessage().contains(message));
        assertFalse(errorResponse.getMessage().contains(queryString));
    }
}
