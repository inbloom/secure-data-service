package org.slc.sli.validation.schema;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnits for FloatSchema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class FloatSchemaTest {
    
    @Autowired
    FloatSchema schema;
    
    @Test
    public void testDecimalValidation() throws IllegalArgumentException {
        assertTrue(schema.validate(1000.00D));
        assertTrue(schema.validate(1000));
        assertTrue(schema.validate(1000L));
        assertTrue(schema.validate(1000.24));
        
        assertFalse(schema.validate("1234"));
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
        
        schema.getProperties().put(Restriction.FRACTION_DIGITS.getValue(), 3);
        assertTrue(schema.validate(1.12D));
        assertTrue(schema.validate(1.123D));
        assertFalse(schema.validate(1.1234D));
        
        schema.getProperties().put(Restriction.TOTAL_DIGITS.getValue(), 3);
        assertTrue(schema.validate(1.12D));
        assertFalse(schema.validate(1.123D));
    }
    
}
