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

package org.slc.sli.modeling.psm;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author jstokes
 */
public class PsmDocumentTest {

    private PsmDocument<String> doc;
    @Before
    public void setup() {
         doc = new PsmDocument<String>("test",
                 new PsmResource("test resource"),
                 new PsmCollection("test collection"));

        // Yay code coverage
        doc.toString();
    }

    @Test
    public void testGetType() {
        assertEquals("test", doc.getType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsNull() {
        new PsmDocument<String>(null, null, null);
    }

    @Test
    public void testGetGraphAssociationEndName() {
        assertEquals("test resource", doc.getGraphAssociationEndName().getName());
    }

    @Test
    public void testGetSingularResourceName() {
        assertEquals("test collection", doc.getSingularResourceName().getName());
    }
}
