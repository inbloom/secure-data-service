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

package org.slc.sli.modeling.xmi.reader;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;

/**
 * Tests a basic POJO.
 *
 * @author kmyers
 *
 */
public class XmiAssociationConnectionTest {

    private static final Range RANGE = new Range(Occurs.ONE, Occurs.ONE);
    private static final Multiplicity MULTIPLICITY = new Multiplicity(RANGE);
    private static final Identifier TYPE = Identifier.fromString("Type");
    private static final AssociationEnd LEFT_HAND_SIDE = new AssociationEnd(MULTIPLICITY, "name1", false, TYPE, "test_end_name");
    private static final AssociationEnd RIGHT_HAND_SIDE = new AssociationEnd(MULTIPLICITY, "name2", true, TYPE, "test_end_name");

    @Test
    public void testConstructorAndGetterSetters() {
        XmiAssociationConnection xmiac = new XmiAssociationConnection(LEFT_HAND_SIDE, RIGHT_HAND_SIDE);
        assertTrue(xmiac.getLHS() == LEFT_HAND_SIDE);
        assertTrue(xmiac.getRHS() == RIGHT_HAND_SIDE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForNullCheckingForLeftHandSide() {
        new XmiAssociationConnection(null, RIGHT_HAND_SIDE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForNullCheckingForRightHandSide() {
        new XmiAssociationConnection(LEFT_HAND_SIDE, null);
    }
}
