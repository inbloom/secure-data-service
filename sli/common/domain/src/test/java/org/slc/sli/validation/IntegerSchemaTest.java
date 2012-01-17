package org.slc.sli.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnits for IntegerSchema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class IntegerSchemaTest {
    
    @Autowired
    IntegerSchema schema;
    
    @Test
    public void testIntegerValidation() throws IllegalArgumentException {
        Integer integerEntity = new Integer(0);
        assertTrue("Integer entity validation failed", schema.validate(integerEntity));
    }
    
    @Test
    public void testPrimitiveIntegerValidation() throws IllegalArgumentException {
        int integerEntity = 0;
        assertTrue("Primitive(int) entity validation failed", schema.validate(integerEntity));
    }
    
    @Test
    public void testValidationOfFloatFailure() {
        Float floatEntity = new Float(0);
        assertFalse("Expected IntegerSchema float validation failure did not succeed", schema.validate(floatEntity));
    }
    
    @Test
    public void testValidationOfStringFailure() {
        String stringEntity = "";
        assertFalse("Expected IntegerSchema string validation failure did not succeed", schema.validate(stringEntity));
    }
    
}
