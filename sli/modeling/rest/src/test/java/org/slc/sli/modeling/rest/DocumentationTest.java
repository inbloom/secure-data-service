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
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.modeling.xdm.DmNode;

/**
 *
 * JUnit test for Documentation class.
 *
 * @author wscott
 *
 */
public class DocumentationTest {

    private Documentation documentation;  //class under test
    private static final String TITLE = "doctest";
    private static final String LANGUAGE = "en";
    private static final List<DmNode> CONTENTS = new ArrayList<DmNode>(0);

    @Before
    public void setup() {
        documentation = new Documentation(TITLE, LANGUAGE, CONTENTS);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testNullContents() {
        new Documentation(TITLE, LANGUAGE, null);
    }

    @Test
    public void testGetTitle() {
        assertEquals(TITLE, documentation.getTitle());
    }

    @Test
    public void testGetLanguage() {
        assertEquals(LANGUAGE, documentation.getLanguage());
    }

    @Test
    public void testGetContents() {
        assertEquals(CONTENTS, documentation.getContents());
    }

    @Test
    public void testToString() {
        assertTrue(!"".equals(documentation.toString()));
    }

}
