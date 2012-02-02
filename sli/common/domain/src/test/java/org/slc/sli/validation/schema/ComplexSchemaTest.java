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
    
    @Test
    public void testComplexValidation() throws IllegalArgumentException {
        schema.getFields().clear();
        schema.getFields().put("booleanField", booleanSchema);
        schema.getFields().put("longField", longSchema);
        schema.getFields().put("doubleField", doubleSchema);
        schema.getFields().put("stringField", stringSchema);
        schema.getFields().put("tokenField", tokenSchema);
        schema.getFields().put("dateTimeField", dateTimeSchema);
        tokenSchema.getProperties().clear();
        List<String> tokens = new ArrayList<String>();
        tokens.add("validToken");
        tokenSchema.getProperties().put(TokenSchema.TOKENS, tokens);
        Map<String, Object> complexEntity = new HashMap<String, Object>();
        Boolean booleanEntity = true;
        Long longEntity = 0L;
        Double doubleEntity = 0.0;
        BigDecimal decimalEntity = new BigDecimal(0);
        String stringEntity = "test";
        String tokenEntity = "validToken";
        String dateTimeEntity = "2012-01-01T12:00:00-05:00";
        complexEntity.put("booleanField", booleanEntity);
        complexEntity.put("longField", longEntity);
        complexEntity.put("doubleField", doubleEntity);
        complexEntity.put("decimalField", decimalEntity);
        complexEntity.put("stringField", stringEntity);
        complexEntity.put("tokenField", tokenEntity);
        complexEntity.put("dateTimeField", dateTimeEntity);
        assertTrue("Complex entity validation failed", schema.validate(complexEntity));
    }
    
    @Test
    public void testComplexFailureValidation() throws IllegalArgumentException {
        schema.getFields().clear();
        schema.getFields().put("booleanField", booleanSchema);
        schema.getFields().put("longField", longSchema);
        schema.getFields().put("doubleField", doubleSchema);
        schema.getFields().put("stringField", stringSchema);
        schema.getFields().put("tokenField", tokenSchema);
        schema.getFields().put("dateTimeField", dateTimeSchema);
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
        hierarchySchema.getFields().clear();
        hierarchySchema.getFields().put("schemaField", schema);
        schema.getFields().clear();
        schema.getFields().put("booleanField", booleanSchema);
        schema.getFields().put("longField", longSchema);
        schema.getFields().put("doubleField", doubleSchema);
        schema.getFields().put("stringField", stringSchema);
        schema.getFields().put("tokenField", tokenSchema);
        schema.getFields().put("dateTimeField", dateTimeSchema);
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
        hierarchyEntity.put("schemaField", complexEntity);
        complexEntity.put("booleanField", booleanEntity);
        complexEntity.put("longField", longEntity);
        complexEntity.put("doubleField", doubleEntity);
        complexEntity.put("decimalField", decimalEntity);
        complexEntity.put("stringField", stringEntity);
        complexEntity.put("tokenField", tokenEntity);
        complexEntity.put("dateTimeField", dateTimeEntity);
        assertTrue("Complex hierarchy entity validation failed", hierarchySchema.validate(hierarchyEntity));
    }
    
    @Test
    public void testComplexHierarchyFailureValidation() throws IllegalArgumentException {
        hierarchySchema.getFields().clear();
        hierarchySchema.getFields().put("schemaField", schema);
        schema.getFields().clear();
        schema.getFields().put("booleanField", booleanSchema);
        schema.getFields().put("longField", longSchema);
        schema.getFields().put("doubleField", doubleSchema);
        schema.getFields().put("stringField", stringSchema);
        schema.getFields().put("tokenField", tokenSchema);
        schema.getFields().put("dateTimeField", dateTimeSchema);
        tokenSchema.getProperties().clear();
        List<String> tokens = new ArrayList<String>();
        tokens.add("validToken");
        tokenSchema.getProperties().put(TokenSchema.TOKENS, tokens);
        Map<String, Object> hierarchyEntity = new HashMap<String, Object>();
        Map<String, Object> complexEntity = new HashMap<String, Object>();
        Long longEntity = 0L;
        Double doubleEntity = 0.0;
        BigDecimal decimalEntity = new BigDecimal(0);
        String stringEntity = "test";
        String tokenEntity = "validToken";
        String dateTimeEntity = "2012-01-01T12:00:00-05:00";
        hierarchyEntity.put("schemaField", complexEntity);
        
        // Setup for field failure
        complexEntity.put("booleanField", stringEntity);
        
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
    public void testComplexHierarchyMapFailureValidation() throws IllegalArgumentException {
        hierarchySchema.getFields().clear();
        hierarchySchema.getFields().put("schemaField", schema);
        schema.getFields().clear();
        schema.getFields().put("booleanField", booleanSchema);
        schema.getFields().put("longField", longSchema);
        schema.getFields().put("doubleField", doubleSchema);
        schema.getFields().put("stringField", stringSchema);
        schema.getFields().put("tokenField", tokenSchema);
        schema.getFields().put("dateTimeField", dateTimeSchema);
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
