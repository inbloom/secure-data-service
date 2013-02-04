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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnits for ComplexSchema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ComplexSchemaTest {

    @Autowired
    ComplexSchema schema;

    @Autowired
    ComplexSchema hierarchySchema;

    @Autowired
    BooleanSchema booleanSchema;

    @Autowired
    LongSchema longSchema;

    @Autowired
    DoubleSchema doubleSchema;

    @Autowired
    StringSchema stringSchema;

    @Autowired
    TokenSchema tokenSchema;

    @Autowired
    DateTimeSchema dateTimeSchema;
    
    @Autowired
    DateTimeSchema integerSchema;

    @Test
    public void testComplexValidation() throws IllegalArgumentException {
        schema.clearFields();
        schema.addField("booleanField", booleanSchema);
        schema.addField("longField", longSchema);
        schema.addField("doubleField", doubleSchema);
        schema.addField("stringField", stringSchema);
        schema.addField("tokenField", tokenSchema);
        schema.addField("dateTimeField", dateTimeSchema);
        tokenSchema.getProperties().clear();
        List<String> tokens = new ArrayList<String>();
        tokens.add("validToken");
        tokenSchema.getProperties().put(TokenSchema.TOKENS, tokens);
        Map<String, Object> complexEntity = new HashMap<String, Object>();
        Boolean booleanEntity = true;
        Long longEntity = 0L;
        Double doubleEntity = 0.0;
        String stringEntity = "test";
        String tokenEntity = "validToken";
        String dateTimeEntity = "2012-01-01T12:00:00-05:00";
        complexEntity.put("booleanField", booleanEntity);
        complexEntity.put("longField", longEntity);
        complexEntity.put("doubleField", doubleEntity);
        complexEntity.put("stringField", stringEntity);
        complexEntity.put("tokenField", tokenEntity);
        complexEntity.put("dateTimeField", dateTimeEntity);
        assertTrue("Complex entity validation failed", schema.validate(complexEntity));
    }
    
    @Test
    public void testInputConversionPerFieldSchema() {
        schema.clearFields();
        schema.addField("booleanField", booleanSchema);
        schema.addField("longField", longSchema);
        schema.addField("doubleField", doubleSchema);
        schema.addField("stringField", stringSchema);
        schema.addField("tokenField", tokenSchema);
        tokenSchema.getProperties().clear();
        List<String> tokens = new ArrayList<String>();
        tokens.add("validToken");
        tokenSchema.getProperties().put(TokenSchema.TOKENS, tokens);
        Map<String, Object> complexEntity = new HashMap<String, Object>();
        
        String booleanEntity = "true";
        String longEntity = "0";
        String doubleEntity = "0.0";
        String stringEntity = "test";
        String tokenEntity = "validToken";
        
        complexEntity.put("booleanField", booleanEntity);
        complexEntity.put("longField", longEntity);
        complexEntity.put("doubleField", doubleEntity);
        complexEntity.put("stringField", stringEntity);
        complexEntity.put("tokenField", tokenEntity);
        
        
        assertTrue("Complex entity validation failed", 
                schema.validate(complexEntity));
        assertTrue("Failed to properly convert boolean input", 
                complexEntity.get("booleanField") instanceof Boolean);
        assertTrue("Failed to properly convert boolean input", 
                complexEntity.get("longField") instanceof Long);
        assertTrue("Failed to properly convert boolean input", 
                complexEntity.get("doubleField") instanceof Double);
        assertTrue("Failed to properly convert boolean input", 
                complexEntity.get("stringField") instanceof String);
        assertTrue("Failed to properly convert boolean input", 
                complexEntity.get("tokenField") instanceof String);
    }

    @Test
    public void testOptionalFields() {
        schema.clearFields();

        BooleanSchema required = new BooleanSchema();
        AppInfo info = new AppInfo(null);
        info.put("required", "true");
        required.addAnnotation(info);

        schema.addField("optionalField", booleanSchema);
        schema.addField("requiredField", required);

        Map<String, Object> complexEntity = new HashMap<String, Object>();
        complexEntity.put("requiredField", Boolean.TRUE);
        assertTrue(schema.validate(complexEntity));

        complexEntity.put("optionalField", Boolean.FALSE);
        assertTrue(schema.validate(complexEntity));

        complexEntity.remove("requiredField");
        assertFalse(schema.validate("requiredField"));
    }

    @Test
    public void testComplexFailureValidation() throws IllegalArgumentException {
        schema.clearFields();
        schema.addField("booleanField", booleanSchema);
        schema.addField("longField", longSchema);
        schema.addField("doubleField", doubleSchema);
        schema.addField("stringField", stringSchema);
        schema.addField("tokenField", tokenSchema);
        schema.addField("dateTimeField", dateTimeSchema);
        tokenSchema.getProperties().clear();
        List<String> tokens = new ArrayList<String>();
        tokens.add("validToken");
        tokenSchema.getProperties().put(TokenSchema.TOKENS, tokens);
        Map<String, Object> complexEntity = new HashMap<String, Object>();
        Long longEntity = 0L;
        Double doubleEntity = 0.0;
        BigDecimal decimalEntity = new BigDecimal(0);
        String stringEntity = "test";
        String tokenEntity = "token";
        String dateTimeEntity = "2012-01-01T12:00:00-05:00";

        // Setup for failure
        complexEntity.put("booleanField", stringEntity);

        complexEntity.put("longField", longEntity);
        complexEntity.put("doubleField", doubleEntity);
        complexEntity.put("decimalField", decimalEntity);
        complexEntity.put("stringField", stringEntity);
        complexEntity.put("tokenField", tokenEntity);
        complexEntity.put("dateTimeField", dateTimeEntity);
        assertFalse("Expected ComplexSchema validation failure did not succeed", schema.validate(complexEntity));
    }

    @Test
    public void testComplexHierarchyValidation() throws IllegalArgumentException {
        hierarchySchema.clearFields();
        hierarchySchema.addField("schemaField", schema);
        schema.clearFields();
        schema.addField("booleanField", booleanSchema);
        schema.addField("longField", longSchema);
        schema.addField("doubleField", doubleSchema);
        schema.addField("stringField", stringSchema);
        schema.addField("tokenField", tokenSchema);
        schema.addField("dateTimeField", dateTimeSchema);
        tokenSchema.getProperties().clear();
        List<String> tokens = new ArrayList<String>();
        tokens.add("validToken");
        tokenSchema.getProperties().put(TokenSchema.TOKENS, tokens);
        Map<String, Object> hierarchyEntity = new HashMap<String, Object>();
        Map<String, Object> complexEntity = new HashMap<String, Object>();
        Boolean booleanEntity = true;
        Long longEntity = 0L;
        Double doubleEntity = 0.0;
        String stringEntity = "test";
        String tokenEntity = "validToken";
        String dateTimeEntity = "2012-01-01T12:00:00-05:00";
        hierarchyEntity.put("schemaField", complexEntity);
        complexEntity.put("booleanField", booleanEntity);
        complexEntity.put("longField", longEntity);
        complexEntity.put("doubleField", doubleEntity);
        complexEntity.put("stringField", stringEntity);
        complexEntity.put("tokenField", tokenEntity);
        complexEntity.put("dateTimeField", dateTimeEntity);
        assertTrue("Complex hierarchy entity validation failed", hierarchySchema.validate(hierarchyEntity));
    }

    @Test
    public void testComplexHierarchyFailureValidation() throws IllegalArgumentException {
        hierarchySchema.clearFields();
        hierarchySchema.addField("schemaField", schema);
        schema.clearFields();
        schema.addField("booleanField", booleanSchema);
        schema.addField("longField", longSchema);
        schema.addField("doubleField", doubleSchema);
        schema.addField("stringField", stringSchema);
        schema.addField("tokenField", tokenSchema);
        schema.addField("dateTimeField", dateTimeSchema);
        tokenSchema.getProperties().clear();
        List<String> tokens = new ArrayList<String>();
        tokens.add("validToken");
        tokenSchema.getProperties().put(TokenSchema.TOKENS, tokens);
        Map<String, Object> hierarchyEntity = new HashMap<String, Object>();
        Map<String, Object> complexEntity = new HashMap<String, Object>();
        Long longEntity = 0L;
        Double doubleEntity = 0.0;
        String stringEntity = "test";
        String tokenEntity = "validToken";
        String dateTimeEntity = "2012-01-01T12:00:00-05:00";
        Boolean booleanEntity = true;
        hierarchyEntity.put("schemaField", complexEntity);

        // Setup for field failure
        complexEntity.put("longField", stringEntity);

        complexEntity.put("booleanField", booleanEntity);
        complexEntity.put("doubleField", doubleEntity);
        complexEntity.put("stringField", stringEntity);
        complexEntity.put("tokenField", tokenEntity);
        complexEntity.put("dateTimeField", dateTimeEntity);
        assertFalse("Expected ComplexSchema hierarchy validation failure did not succeed",
                hierarchySchema.validate(hierarchyEntity));
    }

    @Test
    public void testComplexHierarchyMapFailureValidation() throws IllegalArgumentException {
        hierarchySchema.clearFields();
        hierarchySchema.addField("schemaField", schema);
        schema.clearFields();
        schema.addField("booleanField", booleanSchema);
        schema.addField("longField", longSchema);
        schema.addField("doubleField", doubleSchema);
        schema.addField("stringField", stringSchema);
        schema.addField("tokenField", tokenSchema);
        schema.addField("dateTimeField", dateTimeSchema);
        tokenSchema.getProperties().clear();
        List<String> tokens = new ArrayList<String>();
        tokens.add("validToken");
        tokenSchema.getProperties().put(TokenSchema.TOKENS, tokens);
        Map<String, Object> hierarchyEntity = new HashMap<String, Object>();
        Map<String, Object> complexEntity = new HashMap<String, Object>();
        Boolean booleanEntity = true;
        Long longEntity = 0L;
        Double doubleEntity = 0.0;
        BigDecimal decimalEntity = new BigDecimal(0);
        String stringEntity = "test";
        String tokenEntity = "validToken";
        String dateTimeEntity = "2012-01-01T12:00:00-05:00";

        // Setup for map failure
        hierarchyEntity.put("schemaField", stringEntity);

        complexEntity.put("booleanField", booleanEntity);
        complexEntity.put("longField", longEntity);
        complexEntity.put("doubleField", doubleEntity);
        complexEntity.put("decimalField", decimalEntity);
        complexEntity.put("stringField", stringEntity);
        complexEntity.put("tokenField", tokenEntity);
        complexEntity.put("dateTimeField", dateTimeEntity);
        assertFalse("Expected ComplexSchema hierarchy validation failure did not succeed",
                hierarchySchema.validate(hierarchyEntity));
    }

    @Test
    public void testValidationOfBooleanFailure() {
        Boolean booleanEntity = true;
        assertFalse("Expected ComplexSchema boolean validation failure did not succeed", schema.validate(booleanEntity));
    }

    @Test
    public void testValidationOfIntegerFailure() {
        Integer integerEntity = 0;
        assertFalse("Expected ComplexSchema integer validation failure did not succeed", schema.validate(integerEntity));
    }

    @Test
    public void testValidationOfFloatFailure() {
        Float floatEntity = new Float(0);
        assertFalse("Expected ComplexSchema float validation failure did not succeed", schema.validate(floatEntity));
    }

}
