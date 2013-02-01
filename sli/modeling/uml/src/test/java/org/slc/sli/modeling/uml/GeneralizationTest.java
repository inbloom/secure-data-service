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
package org.slc.sli.modeling.uml;


import org.junit.Before;
import org.junit.Test;
import org.slc.sli.modeling.uml.index.DefaultVisitor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * JUnit test for Generalization
 * @author chung
 */
public class GeneralizationTest {

    private Generalization generalization;
    private Visitor visitor = new DefaultVisitor();

    @Before
    public void setup() {
        generalization = new Generalization("TestGeneralization",
                Identifier.fromString("1234"), Identifier.fromString("5678"));
    }

    @Test
    public void testAccept() {
        generalization.accept(visitor);
    }

    @Test
    public void testGetChild() {
        Identifier child = generalization.getChild();
        assertNotNull(child);
        assertEquals("1234", child.toString());
    }

    @Test
    public void testGetParent() {
        Identifier parent = generalization.getParent();
        assertNotNull(parent);
        assertEquals("5678", parent.toString());
    }

    @Test
    public void testToString() {
        String string1 = generalization.toString();
        String string2 = "{id: " + generalization.getId()
                + ", name: \"TestGeneralization\", parent: \"5678\", child: \"1234\"}";
        assertEquals(string2, string1);
    }

}
