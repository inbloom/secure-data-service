package org.slc.sli.validation.schema;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnits for LongSchema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class LongSchemaTest {
    
    @Autowired
    LongSchema schema;
    
    @Test
    public void testDecimalValidation() throws IllegalArgumentException {
        assertFalse(schema.validate(1000.00D));
        assertTrue(schema.validate(1000));
        assertTrue(schema.validate(1000L));
        assertFalse(schema.validate(1000.24));
        assertTrue(schema.validate("1234"));
        assertFalse(schema.validate("test"));
        assertFalse(schema.validate(""));
    }
    
    @Test
    public void testRestrictions() {
        schema.getProperties().put(Restriction.MIN_INCLUSIVE.getValue(), 0);
        schema.getProperties().put(Restriction.MAX_INCLUSIVE.getValue(), 20);
        
        assertTrue(schema.validate(0));
        assertTrue(schema.validate(1));
        assertTrue(schema.validate(20));
        assertFalse(schema.validate(-1));
        assertFalse(schema.validate(30));
        
        schema.getProperties().put(Restriction.MIN_EXCLUSIVE.getValue(), 0);
        schema.getProperties().put(Restriction.MAX_EXCLUSIVE.getValue(), 19);
        assertFalse(schema.validate(0));
        assertTrue(schema.validate(18));
        assertFalse(schema.validate(19));
    }
    
    @Test
    public void testConvert() throws Exception {
        long value = 10L;
        Object convertedValue = this.schema.convert("" + value);
        assertTrue(convertedValue instanceof Long);
        Long convertedInput = (Long) convertedValue;
        assertTrue(convertedInput.longValue() == value);
    }
    
    @Test
    public void testBadConvert() {
        Object convertedValue = this.schema.convert("INVALID INPUT");
        assertFalse(convertedValue instanceof Long);
    }
    
}
