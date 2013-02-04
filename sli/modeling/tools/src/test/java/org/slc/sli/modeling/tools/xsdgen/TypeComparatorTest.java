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

package org.slc.sli.modeling.tools.xsdgen;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slc.sli.modeling.uml.Type;

/**
 * @author jstokes
 */
public class TypeComparatorTest {
    @Test
    public void testCompare() throws Exception {
        final TypeComparator comparator = TypeComparator.SINGLETON;
        final Type mockType1 = Mockito.mock(Type.class);
        final Type mockType2 = Mockito.mock(Type.class);

        Mockito.when(mockType1.getName()).thenReturn("a");
        Mockito.when(mockType2.getName()).thenReturn("z");
        Assert.assertTrue(comparator.compare(mockType1, mockType2) < 0);

        Mockito.when(mockType1.getName()).thenReturn("z");
        Mockito.when(mockType2.getName()).thenReturn("a");
        Assert.assertTrue(comparator.compare(mockType1, mockType2) > 0);

        Mockito.when(mockType1.getName()).thenReturn("a");
        Mockito.when(mockType2.getName()).thenReturn("a");
        Assert.assertTrue(comparator.compare(mockType1, mockType2) == 0);

        @SuppressWarnings("unused")
        final boolean bool = mockType1.equals(mockType2);

        Mockito.verify(mockType1, Mockito.atLeastOnce()).getName();
        Mockito.verify(mockType2, Mockito.atLeastOnce()).getName();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValueOf() {
        final TypeComparator comparator = TypeComparator.valueOf("test");
    }
}
