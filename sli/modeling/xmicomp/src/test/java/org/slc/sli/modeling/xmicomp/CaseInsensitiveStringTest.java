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

import static org.junit.Assert.*;

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
        assertFalse(STRING1.compareTo(STRING2) == 0);
        assertTrue(CIS1.compareTo(CIS2) == 0);
    }

    @Test
    public void testEquals() {
        assertFalse(CIS1.equals(STRING1));
        assertTrue(CIS1.equals(new CaseInsensitiveString(STRING2)));
    }

    @Test
    public void testHashCode() {
        assertTrue(CIS1.hashCode() == CIS2.hashCode());
    }

    @Test
    public void testToString() {
        assertEquals(CIS1.toString(), CIS2.toString());
    }
}
