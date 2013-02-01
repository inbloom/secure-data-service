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
 * JUnit test for TaggedValue
 * @author chung
 */
public class TaggedValueTest {

    private TaggedValue taggedValue;
    private Visitor visitor = new DefaultVisitor();

    @Before
    public void setup() {
        taggedValue = new TaggedValue(Identifier.fromString("1234"), TestUtils.EMPTY_TAGGED_VALUES,
                "TestValue", Identifier.fromString("TestTagDefn"));
    }

    @Test
    public void testAccept() {
        taggedValue.accept(visitor);
    }

    @Test
    public void testGetTagDefinition() {
        assertEquals("TestTagDefn", taggedValue.getTagDefinition().toString());
    }

    @Test
    public void testGetValue() {
        assertEquals("TestValue", taggedValue.getValue());
    }

    @Test
    public void testToString() {
        String string1 = taggedValue.toString();
        String string2 = "{id: 1234, value: \"TestValue\", tagDefinition: TestTagDefn}";
        assertEquals(string2, string1);
    }

}
