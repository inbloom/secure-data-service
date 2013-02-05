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
import org.slc.sli.modeling.uml.utils.TestUtils;

import static org.junit.Assert.assertEquals;

/**
 * JUnit test for TagDefinition
 * @author chung
 */
public class TagDefinitionTest {

    private TagDefinition tagDefinition;
    private Visitor visitor = new DefaultVisitor();

    @Before
    public void setup() {
        tagDefinition = new TagDefinition(Identifier.fromString("1234"), "TestTagDefinition", TestUtils.ZERO_TO_ONE);
    }

    @Test
    public void testAccept() {
        tagDefinition.accept(visitor);
    }

    @Test
    public void testGetMultiplicity() {
        assertEquals(TestUtils.ZERO_TO_ONE, tagDefinition.getMultiplicity());
    }

    @Test
    public void testToString() {
        String string1 = tagDefinition.toString();
        String string2 = "{id: 1234, name: TestTagDefinition, multiplicity: " + TestUtils.ZERO_TO_ONE + "}";
        assertEquals(string2, string1);
    }

}
