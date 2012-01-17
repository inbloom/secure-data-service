package org.slc.sli.validation;

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
    public void testFloatValidation() throws IllegalArgumentException {
        Float floatEntity = new Float(1.0);
        assertTrue("Float entity validation failed", schema.validate(floatEntity));
    }
    
    @Test
    public void testPrimitiveFloatValidation() throws IllegalArgumentException {
        float floatEntity = (float) 1.0;
        assertTrue("Primitive(float) entity validation failed", schema.validate(floatEntity));
    }
    
    @Test
    public void testValidationOfIntegerFailure() {
        Integer integerEntity = 0;
        assertFalse("Expected FloatSchema integer validation failure did not succeed", schema.validate(integerEntity));
    }
    
    @Test
    public void testValidationOfStringFailure() {
        String stringEntity = "";
        assertFalse("Expected FloatSchema string validation failure did not succeed", schema.validate(stringEntity));
    }
    
}
