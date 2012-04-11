package org.slc.sli.modeling.uml;

import junit.framework.TestCase;

/**
 * Tests for {@link Identifier}.
 */
public final class IdentifierTestCase extends TestCase {
    
    public void testInitializationFromRandom() {
        final Identifier id = Identifier.random();
        final Identifier idCopy = Identifier.fromString(id.toString());
        assertEquals(id, idCopy);
    }
    
    /**
     * Verify that the identifier can also hold plain strings.
     */
    public void testInitializationFromPlainString() {
        final Identifier id = Identifier.fromString("1234");
        assertEquals("1234", id.toString());
    }
}
