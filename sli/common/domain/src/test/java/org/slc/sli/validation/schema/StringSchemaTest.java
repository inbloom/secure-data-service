package org.slc.sli.validation.schema;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnits for StringSchema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class StringSchemaTest {

    @Autowired
    StringSchema schema;

    @Test
    public void testStringValidation() throws IllegalArgumentException {
        String stringEntity = new String("test");
        assertTrue("String entity validation failed", schema.validate(stringEntity));
    }

    @Test
    public void testValidationOfBooleanFailure() {
        Boolean booleanEntity = true;
        assertFalse("Expected StringSchema boolean validation failure did not succeed", schema.validate(booleanEntity));
    }

    @Test
    public void testValidationOfIntegerFailure() {
        Integer integerEntity = 0;
        assertFalse("Expected StringSchema integer validation failure did not succeed", schema.validate(integerEntity));
    }

    @Test
    public void testValidationOfFloatFailure() {
        Float floatEntity = new Float(0);
        assertFalse("Expected StringSchema float validation failure did not succeed", schema.validate(floatEntity));
    }

    @Test
    public void testRestrictions() {
        schema.getProperties().put(Restriction.MIN_LENGTH.getValue(), "2");
        schema.getProperties().put(Restriction.MAX_LENGTH.getValue(), "4");

        assertTrue(schema.validate("12"));
        assertTrue(schema.validate("1234"));
        assertFalse(schema.validate("1"));
        assertFalse(schema.validate("12345"));

        schema.getProperties().put(Restriction.LENGTH.getValue(), "4");
        assertTrue(schema.validate("1234"));
        assertFalse(schema.validate("123"));
        assertFalse(schema.validate("12345"));
    }

}
