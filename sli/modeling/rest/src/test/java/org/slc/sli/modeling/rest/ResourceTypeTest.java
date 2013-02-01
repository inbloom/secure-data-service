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
 * JUnit test for ResourceType class.
 *
 * @author wscott
 *
 */
public class ResourceTypeTest {

    private ResourceType resourceType; // class under test

    private static final String ID = "id";
    private static final String VERB = Method.NAME_HTTP_GET;
    private static final ArrayList<Response> RESPONSES = new ArrayList<Response>(0);
    private static final ArrayList<Representation> REPRESENTATIONS = new ArrayList<Representation>(0);
    private static final ArrayList<Documentation> DOC = new ArrayList<Documentation>(0);
    private static final Request REQUEST = new Request(DOC, new ArrayList<Param>(0), REPRESENTATIONS);
    private static final Method METHOD = new Method(ID, VERB, DOC, REQUEST, RESPONSES);
    private static final ArrayList<String> TYPE = new ArrayList<String>(0);
    private static final String QUERY_TYPE = "queryType";
    private static final String PATH = "path";
    private static final ArrayList<Param> PARAMS = new ArrayList<Param>(0);
    private static final ArrayList<Method> METHODS = new ArrayList<Method>(0);
    private static final ArrayList<Resource> RESOURCES = new ArrayList<Resource>(0);
    private static final String RESOURCE_CLASS = "MyClass";
    private static final Resource RESOURCE = new Resource(ID, TYPE, QUERY_TYPE, PATH, DOC, PARAMS, METHODS, RESOURCES, RESOURCE_CLASS);

    @Before
    public void setup() throws Exception {
        resourceType = new ResourceType(ID, DOC, PARAMS, METHOD, RESOURCE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullId() {
        new ResourceType(null, DOC, PARAMS, METHOD, RESOURCE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullMethod() {
        new ResourceType(ID, DOC, PARAMS, null, RESOURCE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParams() {
        new ResourceType(ID, DOC, null, METHOD, RESOURCE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullResource() {
        new ResourceType(ID, DOC, PARAMS, METHOD, null);
    }

    @Test
    public void testGetId() {
        assertEquals(ID, resourceType.getId());
    }

    @Test
    public void testGetMethod() {
        assertEquals(METHOD, resourceType.getMethod());
    }

    @Test
    public void testGetParams() {
        assertEquals(PARAMS, resourceType.getParams());
    }

    @Test
    public void testGetResource() {
        assertEquals(RESOURCE, resourceType.getResource());
    }

}
