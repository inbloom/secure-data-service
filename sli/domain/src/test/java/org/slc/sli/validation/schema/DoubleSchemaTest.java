package org.slc.sli.validation.schema;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnits for DoubleSchema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class DoubleSchemaTest {
    
    @Autowired
    DoubleSchema schema;
    
    @Test
    public void testDecimalValidation() throws IllegalArgumentException {
        assertTrue(schema.validate(1000.00D));
        assertTrue(schema.validate(1000));
        assertTrue(schema.validate(1000L));
        assertTrue(schema.validate(1000.24));

        assertTrue(schema.validate("1234"));
        assertFalse(schema.validate("test"));
        assertFalse(schema.validate(""));
    }
    
    @Test
    public void testRestrictions() {
        schema.getProperties().put(Restriction.MIN_INCLUSIVE.getValue(), 0D);
        schema.getProperties().put(Restriction.MAX_INCLUSIVE.getValue(), 2D);
        
        assertTrue(schema.validate(0D));
        assertTrue(schema.validate(1D));
        assertTrue(schema.validate(2D));
        assertFalse(schema.validate(-1D));
        assertFalse(schema.validate(2.001D));
        
        schema.getProperties().put(Restriction.MIN_EXCLUSIVE.getValue(), 0D);
        schema.getProperties().put(Restriction.MAX_EXCLUSIVE.getValue(), 2D);
        assertFalse(schema.validate(0D));
        assertTrue(schema.validate(1D));
        assertFalse(schema.validate(2D));
    }
    
    @Test
    public void testConvert() throws Exception {
        double value = 1.2;
        Object convertedValue = this.schema.convert("" + value);
        assertTrue(convertedValue instanceof Double);
        Double convertedInput = (Double) convertedValue;
        assertTrue(convertedInput.doubleValue() == value);
    }
    
    @Test
    public void testBadConvert() {
        Object convertedValue = this.schema.convert("INVALID INPUT");
        assertFalse(convertedValue instanceof Double);
    }
    
}
