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

package org.slc.sli.modeling.xmi.comp;

import org.junit.Test;
import org.junit.Assert;
import javax.xml.namespace.QName;

/**
 * JUnit test for CaseInsensitiveQName class.
 */
public class CaseInsensitiveQNameTest {

    private CaseInsensitiveQName ciqn1 = new CaseInsensitiveQName("tYPe", "fEaTuRe");
    private CaseInsensitiveQName ciqn2 = new CaseInsensitiveQName("TypE", "FeAtUrE");

    @Test
    public void testCompare() {
        Assert.assertTrue(ciqn1.compareTo(ciqn2) == 0);
    }

    @Test
    public void testEquals() {
        Assert.assertTrue(ciqn1.equals(ciqn2));
        Assert.assertFalse(ciqn1.equals(new QName("type", "feature")));
    }

    @Test
    public void testGetLocalPart() {
        Assert.assertEquals(ciqn1.getLocalPart(), ciqn2.getLocalPart());
    }

    @Test
    public void testGetNamespaceUri() {
        Assert.assertEquals(ciqn1.getNamespaceURI(), ciqn2.getNamespaceURI());
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(ciqn1.hashCode(), ciqn2.hashCode());
    }

    @Test
    public void testToString() {
        Assert.assertEquals(ciqn1.toString(), ciqn2.toString());
    }
}
