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
 * JUnit test for Method class.
 *
 * @author wscott
 *
 */
public class MethodTest {

    private Method method; // class under test

    private static final ArrayList<Response> RESPONSES = new ArrayList<Response>(0);
    private static final ArrayList<Representation> REPRESENTATIONS = new ArrayList<Representation>(0);
    private static final String ID = "id";
    private static final String VERB = Method.NAME_HTTP_GET;
    private static final ArrayList<Documentation> DOC = new ArrayList<Documentation>(0);
    private static final Request REQUEST = new Request(DOC, new ArrayList<Param>(0), REPRESENTATIONS);
    private static final String ILLEGAL_VERB = "illegal verb";

    @Before
    public void setup() throws Exception {
        method = new Method(ID, VERB, DOC, REQUEST, RESPONSES);
    }

    @Test
    public void checkAllVerbs() {
        new Method(ID, Method.NAME_HTTP_DELETE, DOC, REQUEST, RESPONSES);
        new Method(ID, Method.NAME_HTTP_GET, DOC, REQUEST, RESPONSES);
        new Method(ID, Method.NAME_HTTP_PATCH, DOC, REQUEST, RESPONSES);
        new Method(ID, Method.NAME_HTTP_POST, DOC, REQUEST, RESPONSES);
        new Method(ID, Method.NAME_HTTP_PUT, DOC, REQUEST, RESPONSES);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullVerb() {
        new Method(ID, null, DOC, REQUEST, RESPONSES);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullResponses() {
        new Method(ID, VERB, DOC, REQUEST, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckIllegalVerb() {
        new Method(ID, ILLEGAL_VERB, DOC, REQUEST, RESPONSES);
    }

    @Test
    public void testGetId() {
        assertEquals(ID, method.getId());
    }

    @Test
    public void testGetRequest() {
        assertEquals(REQUEST, method.getRequest());
    }

    @Test
    public void testGetResponses() {
        assertEquals(RESPONSES, method.getResponses());
    }

    @Test
    public void testGetVerb() {
        assertEquals(VERB, method.getVerb());
    }

    @Test
    public void testToString() {
        assertTrue(!"".equals(method.toString()));
    }

}
