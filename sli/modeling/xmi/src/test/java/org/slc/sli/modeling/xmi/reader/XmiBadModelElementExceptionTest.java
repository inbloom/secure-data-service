package org.slc.sli.modeling.xmi.reader;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests the proper placement of data into a RuntimeException.
 *
 * @author kmyers
 *
 */
public class XmiBadModelElementExceptionTest {

    @Test
    public void testDataStoredCorrectly() {
        RuntimeException re = new IllegalArgumentException();
        String message = "You're doing it wrong.";

        XmiBadModelElementException xmibae = new XmiBadModelElementException(message, re);
        assertTrue(xmibae.getMessage().equals(message));
        assertTrue(xmibae.getCause() == re);
    }
}
