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

/**
 * JUnit test for CaseInsensitiveString class.
 */
public class CaseInsensitiveStringTest {
    private static final String STRING1 = "i am a string";
    private static final String STRING2 = "I aM a StRiNg";

    private static final CaseInsensitiveString CIS1 = new CaseInsensitiveString(STRING1);
    private static final CaseInsensitiveString CIS2 = new CaseInsensitiveString(STRING2);

    @Test
    public void testCompareTo() {
        Assert.assertFalse(STRING1.compareTo(STRING2) == 0);
        Assert.assertTrue(CIS1.compareTo(CIS2) == 0);
    }

    @Test
    public void testEquals() {
        Assert.assertFalse(CIS1.equals(STRING1));
        Assert.assertTrue(CIS1.equals(new CaseInsensitiveString(STRING2)));
    }

    @Test
    public void testHashCode() {
        Assert.assertTrue(CIS1.hashCode() == CIS2.hashCode());
    }

    @Test
    public void testToString() {
        Assert.assertEquals(CIS1.toString(), CIS2.toString());
    }
}
