package org.slc.sli.validation;

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
    public void testDoubleValidation() throws IllegalArgumentException {
        Double doubleEntity = new Double(1.0);
        assertTrue("Double entity validation failed", schema.validate(doubleEntity));
    }
    
    @Test
    public void testPrimitiveDoubleValidation() throws IllegalArgumentException {
        double doubleEntity = 1.0;
        assertTrue("Primitive(double) entity validation failed", schema.validate(doubleEntity));
    }
    
    @Test
    public void testValidationOfIntegerFailure() {
        Integer integerEntity = 0;
        assertFalse("Expected DoubleSchema integer validation failure did not succeed", schema.validate(integerEntity));
    }
    
    @Test
    public void testValidationOfStringFailure() {
        String stringEntity = "";
        assertFalse("Expected DoubleSchema string validation failure did not succeed", schema.validate(stringEntity));
    }
    
}
