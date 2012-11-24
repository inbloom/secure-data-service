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

package org.slc.sli.modeling.xmicomp;

import org.junit.Test;

import javax.xml.namespace.QName;

import static org.junit.Assert.*;

/**
 * JUnit test for CaseInsensitiveQName class.
 */
public class CaseInsensitiveQNameTest {

    private CaseInsensitiveQName ciqn1 = new CaseInsensitiveQName("tYPe", "fEaTuRe");
    private CaseInsensitiveQName ciqn2 = new CaseInsensitiveQName("TypE", "FeAtUrE");

    @Test
    public void testCompare() {
        assertTrue(ciqn1.compareTo(ciqn2) == 0);
    }

    @Test
    public void testEquals() {
        assertTrue(ciqn1.equals(ciqn2));
        assertFalse(ciqn1.equals(new QName("type", "feature")));
    }

    @Test
    public void testGetLocalPart() {
        assertEquals(ciqn1.getLocalPart(), ciqn2.getLocalPart());
    }

    @Test
    public void testGetNamespaceUri() {
        assertEquals(ciqn1.getNamespaceURI(), ciqn2.getNamespaceURI());
    }

    @Test
    public void testHashCode() {
        assertEquals(ciqn1.hashCode(), ciqn2.hashCode());
    }

    @Test
    public void testToString() {
        assertEquals(ciqn1.toString(), ciqn2.toString());
    }
}
