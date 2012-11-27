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

package org.slc.sli.modeling.xsdgen;

import org.junit.Test;
import org.slc.sli.modeling.uml.Type;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * @author jstokes
 */
public class TypeComparatorTest {
    @Test
    public void testCompare() throws Exception {
        final TypeComparator comparator = TypeComparator.SINGLETON;
        final Type mockType1 = mock(Type.class);
        final Type mockType2 = mock(Type.class);

        when(mockType1.getName()).thenReturn("a");
        when(mockType2.getName()).thenReturn("z");
        assertTrue(comparator.compare(mockType1, mockType2) < 0);

        when(mockType1.getName()).thenReturn("z");
        when(mockType2.getName()).thenReturn("a");
        assertTrue(comparator.compare(mockType1, mockType2) > 0);

        when(mockType1.getName()).thenReturn("a");
        when(mockType2.getName()).thenReturn("a");
        assertTrue(comparator.compare(mockType1, mockType2) == 0);

        @SuppressWarnings("unused")
        final boolean bool = mockType1.equals(mockType2);

        verify(mockType1, atLeastOnce()).getName();
        verify(mockType2, atLeastOnce()).getName();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValueOf() {
        final TypeComparator comparator = TypeComparator.valueOf("test");
    }
}
