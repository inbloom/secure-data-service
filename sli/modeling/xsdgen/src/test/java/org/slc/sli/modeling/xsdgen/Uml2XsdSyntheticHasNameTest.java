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
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.ModelIndex;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author jstokes
 */
public class Uml2XsdSyntheticHasNameTest {

    @Test
    public void testGetName() throws Exception {
        Range range;
        Multiplicity multiplicity;
        final Identifier id = Identifier.fromString("1234");
        AssociationEnd mockEnd;
        final ModelIndex mockIndex = mock(ModelIndex.class);
        final Type type = mock(Type.class);

        Uml2XsdSyntheticHasName test;

        when(mockIndex.getType(id)).thenReturn(type);
        when(type.getName()).thenReturn("test");

        range = new Range(Occurs.ZERO,  Occurs.ONE);
        multiplicity = new Multiplicity(range);
        mockEnd = new AssociationEnd(multiplicity, "test", true, id, "test");
        test = new Uml2XsdSyntheticHasName(mockEnd, mockIndex);
        String actual = test.getName();
        String expected = "test";
        assertEquals(actual, expected);

        range = new Range(Occurs.ZERO, Occurs.ZERO);
        multiplicity = new Multiplicity(range);
        mockEnd = new AssociationEnd(multiplicity, "test", true, id, "test");
        test = new Uml2XsdSyntheticHasName(mockEnd, mockIndex);
        actual = test.getName();
        expected = "test";
        assertEquals(actual, expected);

        range = new Range(Occurs.ZERO, Occurs.UNBOUNDED);
        multiplicity = new Multiplicity(range);
        mockEnd = new AssociationEnd(multiplicity, "test", true, id, "test");
        test = new Uml2XsdSyntheticHasName(mockEnd, mockIndex);
        actual = test.getName();
        expected = "tests";
        assertEquals(actual, expected);
    }
}
