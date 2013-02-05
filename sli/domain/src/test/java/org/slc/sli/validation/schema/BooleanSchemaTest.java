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
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidStringThrowsException() throws IllegalArgumentException {
        this.schema.convert("INVALID INPUT");
    }
    

    @Test(expected = IllegalArgumentException.class)
    public void testUnsupportedObjectTypeThrowsException() throws IllegalArgumentException {
        this.schema.convert(new Object());
    }
    
    @Test
    public void testBooleanConverter() {

        assertTrue("Failure returning same object", this.schema.convert(true).equals(Boolean.TRUE));
        assertTrue("Failure returning same object", this.schema.convert(false).equals(Boolean.FALSE));
        assertTrue("Failure parsing true", this.schema.convert("true").equals(Boolean.TRUE));
        assertTrue("Failure parsing false", this.schema.convert("false").equals(Boolean.FALSE));
    }
    
    
}
