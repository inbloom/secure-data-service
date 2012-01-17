package org.slc.sli.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnits for RestrictedSchema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class RestrictedSchemaTest {
    
    @Autowired
    RestrictedSchema schema;
    
    @Test
    public void testRestrictedEmptyValidation() throws IllegalArgumentException {
        schema.getProperties().clear();
        String restrictedEntity = new String("test");
        assertTrue("Restricted entity empty validation failed", schema.validate(restrictedEntity));
    }
    
    @Test
    public void testRestrictedExactLengthValidation() throws IllegalArgumentException {
        schema.getProperties().clear();
        int exactLength = 16;
        schema.getProperties().put(RestrictedSchema.LENGTH, "" + exactLength);
        String restrictedEntity = new String("1234567890123456");
        assertTrue("Restricted entity exact length validation failed", schema.validate(restrictedEntity));
    }
    
    @Test
    public void testRestrictedExactLengthFailureValidation() throws IllegalArgumentException {
        schema.getProperties().clear();
        int exactLength = 16;
        schema.getProperties().put(RestrictedSchema.LENGTH, "" + exactLength);
        String restrictedEntity = new String("12345");
        assertFalse("Expected RestrictedSchema exact length validation failure did not succeed",
                schema.validate(restrictedEntity));
    }
    
    @Test
    public void testRestrictedMinLengthValidation() throws IllegalArgumentException {
        schema.getProperties().clear();
        int minLength = 2;
        schema.getProperties().put(RestrictedSchema.MIN_LENGTH, "" + minLength);
        String restrictedEntity = new String("123");
        assertTrue("Restricted entity min length validation failed", schema.validate(restrictedEntity));
    }
    
    @Test
    public void testRestrictedMinLengthFailureValidation() throws IllegalArgumentException {
        schema.getProperties().clear();
        int minLength = 2;
        schema.getProperties().put(RestrictedSchema.MIN_LENGTH, "" + minLength);
        String restrictedEntity = new String("1");
        assertFalse("Expected RestrictedSchema min length validation failure did not succeed",
                schema.validate(restrictedEntity));
    }
    
    @Test
    public void testRestrictedMaxLengthValidation() throws IllegalArgumentException {
        schema.getProperties().clear();
        int maxLength = 2;
        schema.getProperties().put(RestrictedSchema.MAX_LENGTH, "" + maxLength);
        String restrictedEntity = new String("1");
        assertTrue("Restricted entity max length validation failed", schema.validate(restrictedEntity));
    }
    
    @Test
    public void testRestrictedMaxLengthFailureValidation() throws IllegalArgumentException {
        schema.getProperties().clear();
        int maxLength = 2;
        schema.getProperties().put(RestrictedSchema.MAX_LENGTH, "" + maxLength);
        String restrictedEntity = new String("123");
        assertFalse("Expected RestrictedSchema max length validation failure did not succeed",
                schema.validate(restrictedEntity));
    }
    
    @Test
    public void testRestrictedMinAndMaxLengthValidation() throws IllegalArgumentException {
        schema.getProperties().clear();
        int minLength = 2;
        int maxLength = 4;
        schema.getProperties().put(RestrictedSchema.MIN_LENGTH, "" + minLength);
        schema.getProperties().put(RestrictedSchema.MAX_LENGTH, "" + maxLength);
        String restrictedEntity = new String("123");
        assertTrue("Restricted entity min and max length validation failed", schema.validate(restrictedEntity));
    }
    
    @Test
    public void testRestrictedMinAndMaxLengthFailureValidation() throws IllegalArgumentException {
        schema.getProperties().clear();
        int minLength = 2;
        int maxLength = 4;
        schema.getProperties().put(RestrictedSchema.MIN_LENGTH, "" + minLength);
        schema.getProperties().put(RestrictedSchema.MAX_LENGTH, "" + maxLength);
        String restrictedEntity = new String("12345");
        assertFalse("Expected RestrictedSchema min and max length validation failure did not succeed",
                schema.validate(restrictedEntity));
    }
    
    @Test
    public void testValidationOfBooleanFailure() {
        Boolean booleanEntity = true;
        assertFalse("Expected RestrictedSchema boolean validation failure did not succeed",
                schema.validate(booleanEntity));
    }
    
    @Test
    public void testValidationOfIntegerFailure() {
        Integer integerEntity = 0;
        assertFalse("Expected RestrictedSchema integer validation failure did not succeed",
                schema.validate(integerEntity));
    }
    
    @Test
    public void testValidationOfFloatFailure() {
        Float floatEntity = new Float(0);
        assertFalse("Expected RestrictedSchema float validation failure did not succeed", schema.validate(floatEntity));
    }
    
}
