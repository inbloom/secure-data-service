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
 * JUnit test for Link class.
 *
 * @author wscott
 *
 */

public class LinkTest {
    private Link link; // class under test

    private static final String RESOURCE_TYPE = "resourceType";
    private static final String REL = "rel";
    private static final String REV = "rev";
    private static final ArrayList<Documentation> DOC = new ArrayList<Documentation>(0);

    @Before
    public void setup() throws Exception {
        link = new Link(RESOURCE_TYPE, REL, REV, DOC);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testNullResourceType() {
        new Link(null, REL, REV, DOC);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testNullRel() {
        new Link(RESOURCE_TYPE, null, REV, DOC);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testNullRev() {
        new Link(RESOURCE_TYPE, REL, null, DOC);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testNullDoc() {
        new Link(RESOURCE_TYPE, REL, REV, null);
    }

    @Test
    public void testGetResourceType() {
        assertEquals(RESOURCE_TYPE, link.getResourceType());
    }

    @Test
    public void testGetRelType() {
        assertEquals(REL, link.getRel());
    }

    @Test
    public void testGetRev() {
        assertEquals(REV, link.getRev());
    }

}
