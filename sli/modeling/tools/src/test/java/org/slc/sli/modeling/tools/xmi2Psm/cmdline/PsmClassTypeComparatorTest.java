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

package org.slc.sli.modeling.tools.xmi2Psm.cmdline;

import org.junit.Test;
import org.slc.sli.modeling.psm.PsmDocument;
import org.slc.sli.modeling.uml.Type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * JUnit test for PsmClassTypeComparator
 */
public class PsmClassTypeComparatorTest {

    @Test
    public void testEnumValues() {
        for (PsmDocumentComparator psmDocumentComparator : PsmDocumentComparator.values()) {
            assertEquals(PsmDocumentComparator.valueOf(psmDocumentComparator.toString()), psmDocumentComparator);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSingletonComparison() {
        Type type1 = mock(Type.class);
        Type type2 = mock(Type.class);

        when(type1.getName()).thenReturn("early");
        when(type2.getName()).thenReturn("late");

        PsmDocument<Type> psmDocument1 = mock(PsmDocument.class);
        PsmDocument<Type> psmDocument2 = mock(PsmDocument.class);

        when(psmDocument1.getType()).thenReturn(type1);
        when(psmDocument2.getType()).thenReturn(type2);

        PsmDocumentComparator comparator = PsmDocumentComparator.SINGLETON;

        assertTrue(comparator.compare(psmDocument1, psmDocument2) < 0);
        assertTrue(comparator.compare(psmDocument1, psmDocument1) == 0);
        assertTrue(comparator.compare(psmDocument2, psmDocument1) > 0);
    }
}
