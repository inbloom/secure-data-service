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
    private static final AssociationEnd LEFT_HAND_SIDE = new AssociationEnd(MULTIPLICITY, "name1", false, TYPE);
    private static final AssociationEnd RIGHT_HAND_SIDE = new AssociationEnd(MULTIPLICITY, "name2", true, TYPE);

    @Test
    public void testConstructorAndGetterSetters() {
        XmiAssociationConnection xmiac = new XmiAssociationConnection(LEFT_HAND_SIDE, RIGHT_HAND_SIDE);
        assertTrue(xmiac.getLHS() == LEFT_HAND_SIDE);
        assertTrue(xmiac.getRHS() == RIGHT_HAND_SIDE);
    }

    @Test(expected = NullPointerException.class)
    public void testForNullCheckingForLeftHandSide() {
        new XmiAssociationConnection(null, RIGHT_HAND_SIDE);
    }

    @Test(expected = NullPointerException.class)
    public void testForNullCheckingForRightHandSide() {
        new XmiAssociationConnection(LEFT_HAND_SIDE, null);
    }
}
