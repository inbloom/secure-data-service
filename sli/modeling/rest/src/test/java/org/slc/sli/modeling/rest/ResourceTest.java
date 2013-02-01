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
 * JUnit test for Resource class.
 *
 * @author wscott
 *
 */
public class ResourceTest {

    private Resource resource;

    private static final String ID = "id";
    private static final ArrayList<String> TYPE = new ArrayList<String>(0);
    private static final String QUERY_TYPE = "queryType";
    private static final String PATH = "path";
    private static final ArrayList<Documentation> DOC = new ArrayList<Documentation>(0);
    private static final ArrayList<Param> PARAMS = new ArrayList<Param>(0);
    private static final ArrayList<Method> METHODS = new ArrayList<Method>(0);
    private static final ArrayList<Resource> RESOURCES = new ArrayList<Resource>(0);
    private static final String RESOURCE_CLASS = "MyClass";

    @Before
    public void setup() throws Exception {
        resource = new Resource(ID, TYPE, QUERY_TYPE, PATH, DOC, PARAMS, METHODS, RESOURCES, RESOURCE_CLASS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullType() {
        new Resource(ID, null, QUERY_TYPE, PATH, DOC, PARAMS, METHODS, RESOURCES, RESOURCE_CLASS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullQeuryType() {
        new Resource(ID, TYPE, null, PATH, DOC, PARAMS, METHODS, RESOURCES, RESOURCE_CLASS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParams() {
        new Resource(ID, TYPE, QUERY_TYPE, PATH, DOC, null, METHODS, RESOURCES, RESOURCE_CLASS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullMethods() {
        new Resource(ID, TYPE, QUERY_TYPE, PATH, DOC, PARAMS, null, RESOURCES, RESOURCE_CLASS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullResources() {
        new Resource(ID, TYPE, QUERY_TYPE, PATH, DOC, PARAMS, METHODS, null, RESOURCE_CLASS);
    }

    @Test
    public void testGetId() {
        assertEquals(ID, resource.getId());
    }

    @Test
    public void testGetMethods() {
        assertEquals(METHODS, resource.getMethods());
    }

    @Test
    public void testGetParams() {
        assertEquals(PARAMS, resource.getParams());
    }

    @Test
    public void testGetPath() {
        assertEquals(PATH, resource.getPath());
    }

    @Test
    public void testGetQueryType() {
        assertEquals(QUERY_TYPE, resource.getQueryType());
    }

    @Test
    public void testGetResources() {
        assertEquals(RESOURCES, resource.getResources());
    }

    @Test
    public void testGetType() {
        assertEquals(TYPE, resource.getType());
    }

    @Test
    public void testToString() {
        assertTrue(!"".equals(resource.toString()));
    }

}
