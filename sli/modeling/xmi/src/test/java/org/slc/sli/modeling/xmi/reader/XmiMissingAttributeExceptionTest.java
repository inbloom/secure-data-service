package org.slc.sli.modeling.xmi.reader;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests the proper placement of data into a RuntimeException.
 *
 * @author kmyers
 *
 */
public class XmiMissingAttributeExceptionTest {

    @Test
    public void testDataStoredCorrectly() {
        String message = "You're doing it wrong.";

        XmiMissingAttributeException xmibae = new XmiMissingAttributeException(message);
        assertTrue(xmibae.getMessage().equals(message));
    }
}
