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

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * JUnit test for DataType
 * @author chung
 */
public class DataTypeTest {

    private DataType dataType;
    private Visitor visitor = new DefaultVisitor();

    @Before
    public void setup() {
        dataType = new DataType(Identifier.fromString("1234"), "TestDataType");
    }

    @Test
    public void testAccept() {
        dataType.accept(visitor);
    }

    @Test
    public void testGetLiterals() {
        List<EnumLiteral> literalList = dataType.getLiterals();
        assertNotNull(literalList);
        assertEquals(0, literalList.size());
    }

    @Test
    public void testIsAbstract() {
        assertEquals(false, dataType.isAbstract());
    }

    @Test
    public void testIsClassType() {
        assertEquals(false, dataType.isClassType());
    }

    @Test
    public void testIsDataType() {
        assertEquals(true, dataType.isDataType());
    }

    @Test
    public void testIsEnumType() {
        assertEquals(false, dataType.isEnumType());
    }

    @Test
    public void testToString() {
        String string1 = dataType.toString();
        String string2 = "{id: 1234, name: \"TestDataType\", isAbstract: false}";
        assertEquals(string2, string1);
    }

}
