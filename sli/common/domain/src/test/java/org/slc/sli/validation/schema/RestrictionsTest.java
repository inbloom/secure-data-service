package org.slc.sli.validation.schema;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests for Restrictions class
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class RestrictionsTest {
    
    @Test
    public void testLookup() {
        assertEquals(Restriction.FRACTION_DIGITS, Restriction.fromValue("fraction-digits"));
        assertEquals(null, Restriction.fromValue("Not a value"));
    }
}
