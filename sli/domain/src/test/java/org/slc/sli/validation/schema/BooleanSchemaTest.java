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
        Boolean booleanEntity = Boolean.valueOf(true);
        assertTrue("Boolean(true) entity validation failed", schema.validate(booleanEntity));
    }
    
    @Test
    public void testBooleanFalseValidation() {
        Boolean booleanEntity = Boolean.valueOf(false);
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
    public void testValidationOfEmptyString() throws IOException {
        String stringEntity = "";
        assertFalse("BooleanSchema string validation failed", schema.validate(stringEntity));
    }

    @Test
    public void testValidationOfString() throws IOException {
        String stringEntity = "true";
        assertTrue("BooleanSchema string validation failed", schema.validate(stringEntity));
    }
    
    @Test
    public void testConvert() throws Exception {
        boolean value = true;
        Object convertedValue = this.schema.convert("" + value);
        assertTrue(convertedValue instanceof Boolean);
        Boolean convertedInput = (Boolean) convertedValue;
        assertTrue(convertedInput.booleanValue() == value);
    }
    
    @Test
    public void testBadConvert() {
        Object convertedValue = this.schema.convert("INVALID INPUT");
        assertFalse(convertedValue instanceof Boolean);
    }
    
}
