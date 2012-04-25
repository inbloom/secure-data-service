package org.slc.sli.validation.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testConverterHelper() {
        String booleanValue = "true";
        String integerValue = "123";
        String doubleValue = "1234.45";
        String longValue = "34567";

        Object retValue = NumberUtils.converterHelper(booleanValue, new NumberUtils.Converter() {
            @Override
            public Object convert(Object value) {
                return Boolean.parseBoolean((String) value);
            }
        });
        assertEquals("Should match", true, retValue);

        retValue = NumberUtils.converterHelper(integerValue, new NumberUtils.Converter() {
            @Override
            public Object convert(Object value) {
                return Integer.parseInt((String) value);
            }
        });
        assertEquals("Should match", 123, retValue);

        retValue = NumberUtils.converterHelper(doubleValue, new NumberUtils.Converter() {
            @Override
            public Object convert(Object value) {
                return Double.parseDouble((String) value);
            }
        });
        assertEquals("Should match", 1234.45, retValue);

        retValue = NumberUtils.converterHelper(longValue, new NumberUtils.Converter() {
            @Override
            public Object convert(Object value) {
                return Long.parseLong((String) value);
            }
        });
        assertEquals("Should match", 34567L, retValue);
    }

    @Test
    public void testConverterHelperFailure() {
        String emptyValue = "";
        String integerValue = "test";

        Object retValue = NumberUtils.converterHelper(emptyValue, new NumberUtils.Converter() {
            @Override
            public Object convert(Object value) {
                return Boolean.parseBoolean((String) value);
            }
        });
        assertTrue("Should match", String.class.isInstance(retValue));

        retValue = NumberUtils.converterHelper(integerValue, new NumberUtils.Converter() {
            @Override
            public Object convert(Object value) {
                return Integer.parseInt((String) value);
            }
        });
        assertEquals("Should match", "test", retValue);
        assertTrue("Should match", String.class.isInstance(retValue));

        retValue = NumberUtils.converterHelper(null, new NumberUtils.Converter() {
            @Override
            public Object convert(Object value) {
                return Double.parseDouble((String) value);
            }
        });
        assertNull("Should be null", retValue);
    }
}
