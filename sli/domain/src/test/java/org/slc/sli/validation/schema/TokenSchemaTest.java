/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
        String tokenEntity = "validToken";
        assertTrue("Token entity validation failed", schema.validate(tokenEntity));
    }
    
    @Test
    public void testTokenValidationFailure() throws IllegalArgumentException {
        List<String> tokens = new ArrayList<String>();
        tokens.add("validToken");
        schema.getProperties().put(TokenSchema.TOKENS, tokens);
        String tokenEntity = "invalidToken";
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
