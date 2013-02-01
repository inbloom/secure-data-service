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
 * JUnit test for EnumType
 * @author chung
 */
public class EnumTypeTest {

    private EnumType enumType;
    private Visitor visitor = new DefaultVisitor();

    @Before
    public void setup() {
        List<EnumLiteral> enumLiteralList = Collections.emptyList();
        enumType = new EnumType(Identifier.fromString("1234"), "TestEnumType", enumLiteralList, TestUtils.EMPTY_TAGGED_VALUES);
    }

    @Test
    public void testAccept() {
        enumType.accept(visitor);
    }

    @Test
    public void testGetLiterals() {
        List<EnumLiteral> list = enumType.getLiterals();
        assertNotNull(list);
        assertEquals(0, list.size());
    }

    @Test
    public void testIsAbstract() {
        assertEquals(false, enumType.isAbstract());
    }

    @Test
    public void testIsClassType() {
        assertEquals(false, enumType.isClassType());
    }

    @Test
    public void testIsDataType() {
        assertEquals(false, enumType.isDataType());
    }

    @Test
    public void testIsEnumType() {
        assertEquals(true, enumType.isEnumType());
    }

    @Test
    public void testToString() {
        String string1 = enumType.toString();
        String string2 = "{id: 1234, name: \"TestEnumType\", literals: []}";
        assertEquals(string2, string1);
    }

}
