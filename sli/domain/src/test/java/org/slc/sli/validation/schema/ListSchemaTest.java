/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnits for ListSchema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ListSchemaTest {
    
    private static final Object[] TRUE_AND_FALSE = new Object[] {
            Boolean.TRUE, 
            Boolean.FALSE
    };
    
    private static final Object[] INVALID_TRUE_FALSE_VALUES = new Object[] {
        "NON-TRUE OR FALSE VALUE"
    };
    
    @Autowired
    ListSchema schema;

    @Autowired
    ComplexSchema complexSchema;

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

    @Test
    public void testValidationOfValidBooleanList() throws IllegalArgumentException {
        boolean validationResult = this.testValidationOfBooleanList(TRUE_AND_FALSE);
        assertTrue("Validation of valid boolean list failed", validationResult);
    }

    @Test
    public void testValidationOfInvalidBooleanList() throws IllegalArgumentException {
        boolean validationResult = this.testValidationOfBooleanList(INVALID_TRUE_FALSE_VALUES);
        assertFalse("Validation of invalid boolean list failed", validationResult);
    }
    
    private boolean testValidationOfBooleanList(Object[]listValues) {
        
        //setup schema for list
        this.schema.clearFields();
        this.schema.getList().add(this.booleanSchema);
        
        //setup list with supplied parameter as its only value
        List<Object> valuesList = new ArrayList<Object>();
        for (Object listValue : listValues) {
            valuesList.add(listValue);
        }
        
        //run validation and return result
        return this.schema.validate(valuesList);
    }

    @Test
    public void testRestrictions() {
        schema.clearFields();
        schema.getList().add(longSchema);
        schema.getProperties().put(Restriction.MIN_LENGTH.getValue(), 1);
        schema.getProperties().put(Restriction.MAX_LENGTH.getValue(), 3);
        List<Long> listEntity = new ArrayList<Long>();
        assertFalse(schema.validate(listEntity));
        listEntity.add(1L);
        assertTrue(schema.validate(listEntity));
        listEntity.add(2L);
        assertTrue(schema.validate(listEntity));
        listEntity.add(3L);
        assertTrue(schema.validate(listEntity));
        listEntity.add(4L);
        assertFalse(schema.validate(listEntity));
        schema.getProperties().put(Restriction.LENGTH.getValue(), 2);
        assertFalse(schema.validate(listEntity));
        listEntity.clear();
        assertFalse(schema.validate(listEntity));
        listEntity.add(1L);
        listEntity.add(2L);
        assertTrue(schema.validate(listEntity));
    }

    @Test
    public void testListOfLongValidation() throws IllegalArgumentException {
        schema.clearFields();
        schema.getList().add(longSchema);
        List<Long> listEntity = new ArrayList<Long>();
        Long longEntity = 0L;
        listEntity.add(longEntity);
        assertTrue("List entity long validation failed", schema.validate(listEntity));
    }

    @Test
    public void testListOfLongNonNumberStringValidation() throws IllegalArgumentException {
        schema.clearFields();
        schema.getList().add(longSchema);
        List<String> listEntity = new ArrayList<String>();
        String stringEntity = "test";

        // Setup for failure
        listEntity.add(stringEntity);

        assertFalse("ListSchema long validation failed", schema.validate(listEntity));
    }

    @Test
    public void testListOfStringValidation() throws IllegalArgumentException {
        schema.clearFields();
        schema.getList().add(stringSchema);
        List<String> listEntity = new ArrayList<String>();
        String stringEntity = "test";
        listEntity.add(stringEntity);
        assertTrue("List entity string validation failed", schema.validate(listEntity));
    }

    @Test
    public void testListOfStringFailureValidation() throws IllegalArgumentException {
        schema.clearFields();
        schema.getList().add(stringSchema);
        List<Double> listEntity = new ArrayList<Double>();
        Double doubleEntity = 0.0;

        // Setup for failure
        listEntity.add(doubleEntity);

        assertFalse("Expected ListSchema string validation failure did not succeed", schema.validate(listEntity));
    }

    @Test
    public void testListOfComplexValidation() throws IllegalArgumentException {
        schema.clearFields();
        schema.getList().add(complexSchema);
        complexSchema.clearFields();
        complexSchema.addField("booleanField", booleanSchema);
        complexSchema.addField("longField", longSchema);
        complexSchema.addField("doubleField", doubleSchema);
        complexSchema.addField("stringField", stringSchema);
        complexSchema.addField("tokenField", tokenSchema);
        complexSchema.addField("dateTimeField", dateTimeSchema);
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
        List<Map<String, Object>> listEntity = new ArrayList<Map<String, Object>>();
        listEntity.add(complexEntity);
        assertTrue("List entity complex validation failed", schema.validate(listEntity));
    }

    @Test
    public void testListOfComplexFailureValidation() throws IllegalArgumentException {
        schema.clearFields();
        schema.getList().add(complexSchema);
        List<String> listEntity = new ArrayList<String>();
        String stringEntity = "test";

        // Setup for failure
        listEntity.add(stringEntity);

        assertFalse("Expected ListSchema complex validation failure did not succeed", schema.validate(listEntity));
    }
    
    @Test
    public void testAnnotations() throws IllegalArgumentException {
        schema.clearFields();
        complexSchema.clearFields();
        AppInfo d = new AppInfo(null);
        d.put(AppInfo.READ_ENFORCEMENT_ELEMENT_NAME, new HashSet<String>(Arrays.asList(Right.READ_RESTRICTED.toString())));
        complexSchema.addAnnotation(d);
        schema.getList().add(complexSchema);
        assertTrue("The schema should have a read_restricted annotation", schema.getAppInfo().getReadAuthorities()
                .contains(Right.READ_RESTRICTED));
    }

    @Test
    public void testValidationOfBooleanFailure() {
        Boolean booleanEntity = true;
        assertFalse("Expected ListSchema boolean validation failure did not succeed", schema.validate(booleanEntity));
    }

    @Test
    public void testValidationOfIntegerFailure() {
        Integer integerEntity = 0;
        assertFalse("Expected ListSchema integer validation failure did not succeed", schema.validate(integerEntity));
    }

    @Test
    public void testValidationOfFloatFailure() {
        Float floatEntity = new Float(0);
        assertFalse("Expected ListSchema float validation failure did not succeed", schema.validate(floatEntity));
    }

}
