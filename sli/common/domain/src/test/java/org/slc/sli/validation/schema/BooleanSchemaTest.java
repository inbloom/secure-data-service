package org.slc.sli.validation.schema;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnits for BooleanSchema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class BooleanSchemaTest {
    
    @Autowired
    BooleanSchema schema;
    
    @Test
    public void testBooleanTrueValidation() {
        Boolean booleanEntity = new Boolean(true);
        assertTrue("Boolean(true) entity validation failed", schema.validate(booleanEntity));
    }
    
    @Test
    public void testBooleanFalseValidation() {
        Boolean booleanEntity = new Boolean(false);
        assertTrue("Boolean(false) entity validation failed", schema.validate(booleanEntity));
    }
    
    @Test
    public void testPrimitiveBooleanTrueValidation() throws IOException {
        boolean booleanEntity = true;
        assertTrue("Primitive(true) entity validation failed", schema.validate(booleanEntity));
    }
    
    @Test
    public void testPrimitiveBooleanFalseValidation() throws IOException {
        boolean booleanEntity = false;
        assertTrue("Primitive(false) entity validation failed", schema.validate(booleanEntity));
    }
    
    @Test
    public void testValidationOfStringFailure() throws IOException {
        String stringEntity = "";
        assertFalse("Expected BooleanSchema string validation failure did not succeed", schema.validate(stringEntity));
    }
    
}
