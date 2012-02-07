package org.slc.sli.validation.schema;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnits for TokenSchema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class TokenSchemaTest {

    @Autowired
    TokenSchema schema;

    @Test
    public void testTokenValidation() throws IllegalArgumentException {
        List<String> tokens = new ArrayList<String>();
        tokens.add("validToken");
        schema.getProperties().put(TokenSchema.TOKENS, tokens);
        String tokenEntity = new String("validToken");
        assertTrue("Token entity validation failed", schema.validate(tokenEntity));
    }

    @Test
    public void testTokenValidationFailure() throws IllegalArgumentException {
        List<String> tokens = new ArrayList<String>();
        tokens.add("validToken");
        schema.getProperties().put(TokenSchema.TOKENS, tokens);
        String tokenEntity = new String("invalidToken");
        assertFalse("Expected TokenSchema invalid token validation failure did not succeed",
                schema.validate(tokenEntity));
    }

    @Test
    public void testValidationOfBooleanFailure() {
        Boolean booleanEntity = true;
        assertFalse("Expected TokenSchema boolean validation failure did not succeed", schema.validate(booleanEntity));
    }

    @Test
    public void testValidationOfIntegerFailure() {
        Integer integerEntity = 0;
        assertFalse("Expected TokenSchema integer validation failure did not succeed", schema.validate(integerEntity));
    }

    @Test
    public void testValidationOfFloatFailure() {
        Float floatEntity = new Float(0);
        assertFalse("Expected TokenSchema float validation failure did not succeed", schema.validate(floatEntity));
    }

}
