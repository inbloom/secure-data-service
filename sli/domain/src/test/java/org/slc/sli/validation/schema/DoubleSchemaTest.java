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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnits for DoubleSchema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class DoubleSchemaTest {
    
    @Autowired
    DoubleSchema schema;
    
    @Test
    public void testDecimalValidation() throws IllegalArgumentException {
        assertTrue(schema.validate(1000.00D));
        assertTrue(schema.validate(1000));
        assertTrue(schema.validate(1000L));
        assertTrue(schema.validate(1000.24));

        assertTrue(schema.validate("1234"));
        assertFalse(schema.validate("test"));
        assertFalse(schema.validate(""));
    }
    
    @Test
    public void testRestrictions() {
        schema.getProperties().put(Restriction.MIN_INCLUSIVE.getValue(), 0D);
        schema.getProperties().put(Restriction.MAX_INCLUSIVE.getValue(), 2D);
        
        assertTrue(schema.validate(0D));
        assertTrue(schema.validate(1D));
        assertTrue(schema.validate(2D));
        assertFalse(schema.validate(-1D));
        assertFalse(schema.validate(2.001D));
        
        schema.getProperties().put(Restriction.MIN_EXCLUSIVE.getValue(), 0D);
        schema.getProperties().put(Restriction.MAX_EXCLUSIVE.getValue(), 2D);
        assertFalse(schema.validate(0D));
        assertTrue(schema.validate(1D));
        assertFalse(schema.validate(2D));
    }
    
    @Test
    public void testConvert() throws Exception {
        double value = 1.2;
        Object convertedValue = this.schema.convert("" + value);
        assertTrue(convertedValue instanceof Double);
        Double convertedInput = (Double) convertedValue;
        assertTrue(convertedInput.doubleValue() == value);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testBadConvert() {
        this.schema.convert("INVALID INPUT");
    }

    @Test
    public void testNonConvert() {
        Object convertedValue = this.schema.convert(12.345);
        assertTrue(convertedValue instanceof Double);
    }

    @Test
    public void testDoubleConverter() {
        
        double data = 12.0;
        float floatData = (float) data;
        int intData = (int) data;
        long longData = (long) data;
        
        
        assertTrue("Failure returning same object",
                this.schema.convert(data).equals(data));
        assertTrue("Failure parsing double from long",
                this.schema.convert(longData).equals(data));
        assertTrue("Failure parsing double from integer",
                this.schema.convert(intData).equals(data));
        assertTrue("Failure parsing double from float",
                this.schema.convert(floatData).equals(data));
        assertTrue("Failure parsing double data", 
                this.schema.convert("" + data).equals(data));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidStringThrowsException() throws IllegalArgumentException {
        this.schema.convert("INVALID INPUT");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testUnsupportedObjectTypeThrowsException() throws IllegalArgumentException {
        this.schema.convert(new Object());
    }
    
    
}
