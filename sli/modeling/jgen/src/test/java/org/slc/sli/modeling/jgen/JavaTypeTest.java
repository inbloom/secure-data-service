/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.modeling.jgen;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * JUnit for JavaType
 * @author chung
 */
public class JavaTypeTest {

    JavaType baseType = JavaType.JT_STRING;

    @Test
    public void testComplexType() {
        JavaType type = JavaType.complexType("TestType", baseType);
        assertTrue(type.getCollectionKind().equals(JavaCollectionKind.NONE));
        assertTrue(type.getTypeKind().equals(JavaTypeKind.COMPLEX));
    }

    @Test
    public void testEnumType() {
        JavaType type = JavaType.enumType("TestType", baseType);
        assertTrue(type.getCollectionKind().equals(JavaCollectionKind.NONE));
        assertTrue(type.getTypeKind().equals(JavaTypeKind.ENUM));
    }

    @Test
    public void testCollectionType() {
        JavaType type = JavaType.collectionType(JavaCollectionKind.HASH_MAP, baseType);
        assertTrue(type.getSimpleName().equals("String"));
        assertTrue(type.getCollectionKind().equals(JavaCollectionKind.HASH_MAP));
        assertTrue(type.getTypeKind().equals(JavaTypeKind.SIMPLE));
    }

    @Test
    public void testGetBaseType() {
        JavaType type = JavaType.complexType("TestType", baseType);
        assertTrue(type.getBaseType().equals(JavaType.JT_STRING));
    }

    @Test
    public void testHashCode() {
        JavaType type = JavaType.enumType("TestType", baseType);
        assertTrue(type.hashCode() == "TestType".hashCode());
    }

    @Test
    public void testPrimeType() {
        JavaType type = JavaType.enumType("TestType", baseType);
        assertTrue(type.primeType().equals(type));
    }

    @Test
    public void testToString() {
        JavaType type = JavaType.complexType("TestType", baseType);
        String str = type.toString();
        assertTrue(str.equals("{name : TestType, collectionKind : " + JavaCollectionKind.NONE
                + ", typeKind : " + JavaTypeKind.COMPLEX + "}"));
    }

}
