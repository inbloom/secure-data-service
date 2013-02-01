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

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * JUnit test for ClassType
 * @author chung
 */
public class ClassTypeTest {

    private ClassType classType;

    private AssociationEnd lhs;
    private AssociationEnd rhs;

    private Visitor visitor = new DefaultVisitor();

    @Before
    public void setup() {
        lhs = new AssociationEnd(TestUtils.ZERO_TO_ONE, "LHS", true, Identifier.fromString("1234"),
                TestUtils.EMPTY_TAGGED_VALUES, Identifier.fromString("LHSType"), "test_end_name");
        rhs = new AssociationEnd(TestUtils.ONE_TO_MANY, "RHS", true, Identifier.fromString("4321"),
                TestUtils.EMPTY_TAGGED_VALUES, Identifier.fromString("RHSType"), "test_end_name");
        classType = new ClassType(lhs, rhs);
    }

    @Test
    public void testAccept() {
        classType.accept(visitor);
    }

    @Test
    public void testGetAttributes() {
        List<Attribute> attributes = classType.getAttributes();
        assertNotNull(attributes);
        assertEquals(0, attributes.size());
    }

    @Test
    public void testGetLHS() {
        AssociationEnd assocEnd = classType.getLHS();
        assertNotNull(assocEnd);
        assertEquals(assocEnd.getId().toString(), "1234");
    }

    @Test
    public void testGetRHS() {
        AssociationEnd assocEnd = classType.getRHS();
        assertNotNull(assocEnd);
        assertEquals(assocEnd.getId().toString(), "4321");
    }

    @Test
    public void testIsAbstract() {
        assertEquals(false, classType.isAbstract());
    }

    @Test
    public void testIsAssociation() {
        assertEquals(true, classType.isAssociation());
    }

    @Test
    public void testIsClassType() {
        assertEquals(false, classType.isClassType());
    }

    @Test
    public void testIsDataType() {
        assertEquals(false, classType.isDataType());
    }

    @Test
    public void testIsEnumType() {
        assertEquals(false, classType.isEnumType());
    }

    @Test
    public void testToString() {
        String string1 = classType.toString();
        String string2 = "{id: " + classType.getId() + ", name: , attributes: []}";
        assertEquals(string2, string1);
    }

}
