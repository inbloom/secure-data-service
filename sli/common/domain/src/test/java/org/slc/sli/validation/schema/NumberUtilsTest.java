package org.slc.sli.validation.schema;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests for NumberUtils
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class NumberUtilsTest {
    
    @Test
    public void testToDouble() {
        assertEquals(Double.valueOf(3), NumberUtils.toDouble(Integer.valueOf(3)));
        assertEquals(Double.valueOf(3), NumberUtils.toDouble(Integer.valueOf(3)));
        assertEquals(null, NumberUtils.toDouble("3"));
    }
    
    @Test
    public void testToLong() {
        assertEquals(Long.valueOf(3), NumberUtils.toLong(Integer.valueOf(3)));
        assertEquals(Long.valueOf(3), NumberUtils.toLong(Long.valueOf(3)));
        assertEquals(null, NumberUtils.toDouble("3"));
    }
}
