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
 * JUnit test for Include class.
 *
 * @author wscott
 *
 */
public class IncludeTest {

    private Include include;

    private static final ArrayList<Documentation> DOC = new ArrayList<Documentation>(0);
    private static final String HREF = "href";

    @Before
    public void setup() throws Exception {
        include = new Include(HREF, DOC);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullHref() {
        new Include(null, DOC);
    }

    @Test
    public void testGetHref() {
        assertEquals(HREF, include.getHref());
    }

}
