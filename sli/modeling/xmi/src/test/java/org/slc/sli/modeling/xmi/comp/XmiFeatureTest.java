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

package org.slc.sli.modeling.xmi.comp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * JUnit test for XmiFeature class.
 */
public class XmiFeatureTest {

    private static final String NAME = "NAME";
    private static final boolean EXISTS = true;
    private static final String CLASS_NAME = "CLASS_NAME";
    private static final boolean CLASS_EXISTS = true;

    private static final XmiFeature XMI_FEATURE = new XmiFeature(NAME, EXISTS, CLASS_NAME, CLASS_EXISTS);

    @Test
    public void testGetName() {
        assertEquals(NAME, XMI_FEATURE.getName());
    }

    @Test
    public void testExists() {
        assertEquals(EXISTS, XMI_FEATURE.exists());
    }

    @Test
    public void testGetOwnerName() {
        assertEquals(CLASS_NAME, XMI_FEATURE.getOwnerName());
    }

    @Test
    public void testOwnerExists() {
        assertEquals(CLASS_EXISTS, XMI_FEATURE.ownerExists());
    }

    @Test
    public void testToString() {

        // just test not null response
        assertNotNull(XMI_FEATURE.toString());

        // if response matters, re-enable this:

        /*

          String expectedResponse = "{name : NAME, exists : true, className : CLASS_NAME, classExists : true}";
          String receivedResponse = XMI_FEATURE.toString();

          assertEquals(expectedResponse, receivedResponse);

          */
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParam1() {
        new XmiFeature(null, EXISTS, CLASS_NAME, CLASS_EXISTS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParam2() {
        new XmiFeature(NAME, EXISTS, null, CLASS_EXISTS);
    }

}
