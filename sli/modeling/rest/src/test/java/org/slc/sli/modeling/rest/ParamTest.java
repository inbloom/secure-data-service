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

import javax.xml.namespace.QName;

import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test for Param class.
 *
 * @author wscott
 *
 */
public class ParamTest {

    private Param param; // class under test

    private static final String NAME = "name";
    private static final String ID = "id";
    private static final QName TYPE = new QName("qname");
    private static final ParamStyle STYLE = ParamStyle.QUERY;
    private static final String PATH = "path";
    private static final String FIXED = "fixed";
    private static final boolean REPEATING = true;
    private static final boolean REQUIRED = true;
    private static final String DEFAULT_VALUE = "defaultValue";
    private static final ArrayList<Documentation> DOC = new ArrayList<Documentation>(
            0);
    private static final String RESOURCE_TYPE = "resourceType";
    private static final String REL = "rel";
    private static final String REV = "rev";
    private static final ArrayList<Option> OPTIONS = new ArrayList<Option>(0);
    private static final Link LINK = new Link(RESOURCE_TYPE, REL, REV, DOC);

    @Before
    public void setup() throws Exception {
        param = new Param(NAME, STYLE, ID, TYPE, DEFAULT_VALUE, REQUIRED,
                REPEATING, FIXED, PATH, DOC, OPTIONS, LINK);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testNullName() {
        new Param(null, STYLE, ID, TYPE, DEFAULT_VALUE, REQUIRED, REPEATING,
                FIXED, PATH, DOC, OPTIONS, LINK);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testNullStyle() {
        new Param(NAME, null, ID, TYPE, DEFAULT_VALUE, REQUIRED, REPEATING,
                FIXED, PATH, DOC, OPTIONS, LINK);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testNullOptions() {
        new Param(NAME, STYLE, ID, TYPE, DEFAULT_VALUE, REQUIRED, REPEATING,
                FIXED, PATH, DOC, null, LINK);
    }

    @Test
    public void testGetName() {
        assertEquals(NAME, param.getName());
    }

    @Test
    public void testGetStyle() {
        assertEquals(STYLE, param.getStyle());
    }

    @Test
    public void testGetId() {
        assertEquals(ID, param.getId());
    }

    @Test
    public void testGetType() {
        assertEquals(TYPE, param.getType());
    }

    @Test
    public void testGetDefault() {
        assertEquals(DEFAULT_VALUE, param.getDefault());
    }

    @Test
    public void testGetRequired() {
        assertEquals(REQUIRED, param.getRequired());
    }

    @Test
    public void testGetRepeating() {
        assertEquals(REPEATING, param.getRepeating());
    }

    @Test
    public void testGetFixed() {
        assertEquals(FIXED, param.getFixed());
    }

    @Test
    public void testGetPath() {
        assertEquals(PATH, param.getPath());
    }

    @Test
    public void testGetOptions() {
        assertEquals(OPTIONS, param.getOptions());
    }

    @Test
    public void testGetLink() {
        assertEquals(LINK, param.getLink());
    }

    @Test
    public void testToString() {
        assertTrue(!"".equals(param.toString()));
    }

}
