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
 * JUnit test for Resources class.
 *
 * @author wscott
 *
 */
public class ResourcesTest {

    private Resources resources;

    private static final String BASE = "base";
    private static final ArrayList<Documentation> DOC = new ArrayList<Documentation>(0);
    private static final ArrayList<Resource> RESOURCE_LIST = new ArrayList<Resource>(0);

    @Before
    public void setup() throws Exception {
        resources = new Resources(BASE, DOC, RESOURCE_LIST);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullBase() {
        new Resources(null, DOC, RESOURCE_LIST);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullResourceList() {
        new Resources(BASE, DOC, null);
    }

    @Test
    public void testGetBase() {
        assertEquals(BASE, resources.getBase());
    }

    @Test
    public void testGetResources() {
        assertEquals(RESOURCE_LIST, resources.getResources());
    }

    @Test
    public void testToString() {
        assertTrue(!"".equals(resources.toString()));
    }

}
