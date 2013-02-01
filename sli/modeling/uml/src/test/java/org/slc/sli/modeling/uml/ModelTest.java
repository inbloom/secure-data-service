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

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * JUnit test for Model
 * @author chung
 */
public class ModelTest {

    private Model model;
    private Visitor visitor = new DefaultVisitor();

    @Before
    public void setup() {
        List<NamespaceOwnedElement> list = Collections.emptyList();
        model = new Model(Identifier.fromString("1234"), "TestModel", TestUtils.EMPTY_TAGGED_VALUES, list);
    }

    @Test
    public void testAccept() {
        model.accept(visitor);
    }

    @Test
    public void testGetName() {
        assertEquals("TestModel", model.getName());
    }

    @Test
    public void testGetOwnedElements() {
        List<NamespaceOwnedElement> list = model.getOwnedElements();
        assertNotNull(list);
        assertEquals(0, list.size());
    }

    @Test
    public void testToString() {
        String string1 = model.toString();
        String string2 = "{id: 1234, name: \"TestModel\", }";
        assertEquals(string2, string1);
    }

}
