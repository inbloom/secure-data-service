package org.slc.sli.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnits for DecimalSchema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class DecimalSchemaTest {
    
    @Autowired
    DecimalSchema schema;
    
    @Test
    public void testDecimalValidation() throws IllegalArgumentException {
        BigDecimal decimalEntity = new BigDecimal("1000.00");
        assertTrue("Decimal entity validation failed", schema.validate(decimalEntity));
    }
    
    @Test
    public void testValidationOfIntegerFailure() {
        Integer integerEntity = 0;
        assertFalse("Expected DecimalSchema integer validation failure did not succeed", schema.validate(integerEntity));
    }
    
    @Test
    public void testValidationOfStringFailure() {
        String stringEntity = "";
        assertFalse("Expected DecimalSchema string validation failure did not succeed", schema.validate(stringEntity));
    }
    
}
