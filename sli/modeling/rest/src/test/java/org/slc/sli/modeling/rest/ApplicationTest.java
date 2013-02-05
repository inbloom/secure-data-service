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
 * JUnit test for Application class.
 *
 * @author wscott
 *
 */
public class ApplicationTest {
    private Application application; // class under test

    private static final ArrayList<Documentation> DOC = new ArrayList<Documentation>(0);
    private static final Grammars GRAMMARS = new Grammars(DOC, new ArrayList<Include>(0));
    private static final Resources RESOURCES = new Resources("base", DOC, new ArrayList<Resource>(0));
    private static final ArrayList<ResourceType> RESOURCE_TYPES = new ArrayList<ResourceType>(0);
    private static final ArrayList<Method> METHODS = new ArrayList<Method>(0);
    private static final ArrayList<Representation> REPRESENTATIONS = new ArrayList<Representation>(0);

    @Before
    public void setup() throws Exception {

        application = new Application(DOC, GRAMMARS, RESOURCES, RESOURCE_TYPES, METHODS, REPRESENTATIONS,
                REPRESENTATIONS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullResourceTypes() {
        new Application(DOC, GRAMMARS, RESOURCES, null, METHODS, REPRESENTATIONS, REPRESENTATIONS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullMethods() {
        new Application(DOC, GRAMMARS, RESOURCES, RESOURCE_TYPES, null, REPRESENTATIONS, REPRESENTATIONS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullRepresentations() {
        new Application(DOC, GRAMMARS, RESOURCES, RESOURCE_TYPES, METHODS, null, REPRESENTATIONS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullFaults() {
        new Application(DOC, GRAMMARS, RESOURCES, RESOURCE_TYPES, METHODS, REPRESENTATIONS, null);
    }

    @Test
    public void testGetGrammars() {
        assertEquals(GRAMMARS, application.getGrammars());
    }

    @Test
    public void testGetResources() {
        assertEquals(RESOURCES, application.getResources());
    }

    @Test
    public void testGetResourceTypes() {
        assertEquals(RESOURCE_TYPES, application.getResourceTypes());
    }

    @Test
    public void testGetMethods() {
        assertEquals(METHODS, application.getMethods());
    }

    @Test
    public void testGetRepresentations() {
        assertEquals(REPRESENTATIONS, application.getRepresentations());
    }

    @Test
    public void testGetFaults() {
        assertEquals(REPRESENTATIONS, application.getFaults());
    }

    @Test
    public void testToString() {
        assertTrue(!"".equals(application.toString()));
    }

}
