/**
 * 
 */
package org.slc.sli.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Takashi Osako
 * Test class for Constants
 */
public class ConstantsTest {
    @Test
    public void test() {
        Constants.FREParticipation fre = Constants.FREParticipation.FREE;
        assertEquals("Value should be \"Free\"", "Free", fre.getValue());
        fre = Constants.FREParticipation.REDUCED_PRICE;
        assertEquals("Value should be \"Reduced Price\"", "Reduced Price", fre.getValue());
    }
}
