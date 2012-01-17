package org.slc.sli.validation;

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
    public void testLongValidation() throws IllegalArgumentException {
        Long longEntity = new Long(0);
        assertTrue("Long entity validation failed", schema.validate(longEntity));
    }
    
    @Test
    public void testPrimitiveLongValidation() throws IllegalArgumentException {
        long longEntity = 0;
        assertTrue("Primitive(long) entity validation failed", schema.validate(longEntity));
    }
    
    @Test
    public void testValidationOfFloatFailure() {
        Float floatEntity = new Float(0);
        assertFalse("Expected LongSchema float validation failure did not succeed", schema.validate(floatEntity));
    }
    
    @Test
    public void testValidationOfStringFailure() {
        String stringEntity = "";
        assertFalse("Expected LongSchema string validation failure did not succeed", schema.validate(stringEntity));
    }
    
}
