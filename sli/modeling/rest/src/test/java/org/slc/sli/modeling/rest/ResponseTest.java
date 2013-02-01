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

package org.slc.sli.modeling.rest;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test for Response class.
 *
 * @author wscott
 *
 */
public class ResponseTest {
    private static final ArrayList<String> STATUS_CODES = new ArrayList<String>(0);
    private static final ArrayList<Documentation> DOC = new ArrayList<Documentation>(0);
    private static final ArrayList<Param> PARAMS = new ArrayList<Param>(0);
    private static final ArrayList<Representation> REPRESENTATIONS = new ArrayList<Representation>(0);
    private Response response;

    @Before
    public void setup() throws Exception {
        response = new Response(STATUS_CODES, DOC, PARAMS, REPRESENTATIONS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullStatusCodes() {
        new Response(null, DOC, PARAMS, REPRESENTATIONS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParams() {
        new Response(STATUS_CODES, DOC, null, REPRESENTATIONS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullRepresentations() {
        new Response(STATUS_CODES, DOC, PARAMS, null);
    }

    @Test
    public void testGetStatusCodes() {
        assertEquals(STATUS_CODES, response.getStatusCodes());
    }

    @Test
    public void testGetParams() {
        assertEquals(PARAMS, response.getParams());
    }

    @Test
    public void testGetRepresentations() {
        assertEquals(REPRESENTATIONS, response.getRepresentations());
    }

    @Test
    public void testToString() {
        assertTrue(!"".equals(response.toString()));
    }

}
