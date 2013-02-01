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
import org.slc.sli.modeling.uml.*;
import org.slc.sli.modeling.uml.index.ModelIndex;

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
        final ModelIndex mockIndex = Mockito.mock(ModelIndex.class);
        final Type type = Mockito.mock(Type.class);

        Uml2XsdSyntheticHasName test;

        Mockito.when(mockIndex.getType(id)).thenReturn(type);
        Mockito.when(type.getName()).thenReturn("test");

        range = new Range(Occurs.ZERO,  Occurs.ONE);
        multiplicity = new Multiplicity(range);
        mockEnd = new AssociationEnd(multiplicity, "test", true, id, "test");
        test = new Uml2XsdSyntheticHasName(mockEnd, mockIndex);
        String actual = test.getName();
        String expected = "test";
        Assert.assertEquals(actual, expected);

        range = new Range(Occurs.ZERO, Occurs.ZERO);
        multiplicity = new Multiplicity(range);
        mockEnd = new AssociationEnd(multiplicity, "test", true, id, "test");
        test = new Uml2XsdSyntheticHasName(mockEnd, mockIndex);
        actual = test.getName();
        expected = "test";
        Assert.assertEquals(actual, expected);

        range = new Range(Occurs.ZERO, Occurs.UNBOUNDED);
        multiplicity = new Multiplicity(range);
        mockEnd = new AssociationEnd(multiplicity, "test", true, id, "test");
        test = new Uml2XsdSyntheticHasName(mockEnd, mockIndex);
        actual = test.getName();
        expected = "tests";
        Assert.assertEquals(actual, expected);
    }
}
