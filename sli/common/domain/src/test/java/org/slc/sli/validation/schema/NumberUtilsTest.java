package org.slc.sli.validation.schema;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;

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
    public void testTotalDigits() {
        assertEquals(4, NumberUtils.totalDigits(1024L));
        assertEquals(4, NumberUtils.totalDigits(1.024D));
        assertEquals(1, NumberUtils.totalDigits(1.000000000000D));
        assertEquals(13, NumberUtils.totalDigits(1024000000000L));
        assertEquals(15, NumberUtils.totalDigits(10000.0000000001D));
        assertEquals(4, NumberUtils.totalDigits(-1024L));
        assertEquals(4, NumberUtils.totalDigits(-1.024D));
    }
    
    @Test
    public void testFractionalDigits() {
        assertEquals(3, NumberUtils.fractionalDigits(1.024D));
        assertEquals(3, NumberUtils.fractionalDigits(-1.024D));
        assertEquals(1, NumberUtils.fractionalDigits(1.10D));
        assertEquals(0, NumberUtils.fractionalDigits(1.000000000000D));
        assertEquals(10, NumberUtils.fractionalDigits(-10000.0000000001D));
        assertEquals(3, NumberUtils.fractionalDigits(-10000.0010000000000D));
    }
    
    @Test
    public void testToDouble() {
        assertEquals(new Double(3), NumberUtils.toDouble(new Integer(3)));
        assertEquals(new Double(3), NumberUtils.toDouble(new Long(3)));
        assertEquals(new Double(3), NumberUtils.toDouble(BigInteger.valueOf(3)));
        assertEquals(new Double(3.01), NumberUtils.toDouble(BigDecimal.valueOf(3.01)));
        assertEquals(null, NumberUtils.toDouble("3"));
    }
    
    @Test
    public void testToLong() {
        assertEquals(new Long(3), NumberUtils.toLong(new Integer(3)));
        assertEquals(new Long(3), NumberUtils.toLong(new Long(3)));
        assertEquals(new Long(3), NumberUtils.toLong(new Float(3.1)));
        assertEquals(new Long(3), NumberUtils.toLong(BigInteger.valueOf(3)));
        assertEquals(new Long(3), NumberUtils.toLong(BigDecimal.valueOf(3.01)));
        assertEquals(null, NumberUtils.toDouble("3"));
    }
}
