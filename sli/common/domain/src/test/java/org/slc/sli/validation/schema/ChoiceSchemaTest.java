package org.slc.sli.validation.schema;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * JUnit for choice schemas
 * 
 * @author nbrown
 * 
 */
public class ChoiceSchemaTest {
    
    NeutralSchema choiceSchema;
    NeutralSchema stringChoice = new StringSchema();
    NeutralSchema intChoice = new IntegerSchema();
    List<NeutralSchema> choiceTypes;
    List<Object> choices;
    
    @Before
    public void init() {
        choiceSchema = null;
        
        choiceTypes = new LinkedList<NeutralSchema>();
        choiceTypes.add(stringChoice);
        choiceTypes.add(intChoice);
        
        choices = new LinkedList<Object>();
    }
    
    @Test
    public void testMinOccursZero() {
        choiceSchema = new ChoiceSchema(0, ChoiceSchema.UNBOUNDED, null);
        
        // minOccurs = 0
        assertTrue(choiceSchema.validate(choices));
    }
    
    @Test
    public void testMinOccursOne() {
        // maxOccurs = minOccurs = 1
        choiceSchema = new ChoiceSchema(1, 1, choiceTypes);
        
        choices.add("a");
        assertTrue(choiceSchema.validate(choices));
    }
    
    @Test
    public void testMinMaxOccursTwo() {
        // minOccurs = 2 maxOccurs = 2
        choiceSchema = new ChoiceSchema(2, 2, choiceTypes);
        
        choices.add(32);
        choices.add("b");
        assertTrue(choiceSchema.validate(choices));
    }
    
    @Test
    public void testMinMaxOccursTwoInvalid() {
        choiceSchema = new ChoiceSchema(2, 2, choiceTypes);
        
        choices.add("c");
        assertFalse(choiceSchema.validate(choiceTypes));
    }
    
    @Test
    public void testMinOneUnboundedMax() {
        // minOccurs = 1 maxOccurs = Unbounded.
        choiceSchema = new ChoiceSchema(1, ChoiceSchema.UNBOUNDED, choiceTypes);
        assertFalse(choiceSchema.validate(choiceTypes));
        
        choices.add("a");
        choices.add(1);
        choices.add("b");
        choices.add(42);
        assertTrue(choiceSchema.validate(choices));
    }
    
    @Test
    public void testComplexChoice() {
        NeutralSchema complex = new ComplexSchema();
        
        StringSchema stringSchema = new StringSchema();
        AppInfo info = new AppInfo(null);
        info.put("required", "true");
        stringSchema.addAnnotation(info);
        
        ListSchema listSchema = new ListSchema();
        listSchema.addAnnotation(info);
        
        BooleanSchema booleanSchema = new BooleanSchema();
        booleanSchema.addAnnotation(info);
        
        complex.addField("stringValue", new StringSchema());
        complex.addField("listValue", new ListSchema());
        complex.addField("booleanValue", new BooleanSchema());
        
        choiceTypes.add(complex);
        
        choiceSchema = new ChoiceSchema(1, 1, choiceTypes);
        choices.add("a");
        assertTrue(choiceSchema.validate(choices));
        
        choices.clear();
        choices.add(75);
        assertTrue(choiceSchema.validate(choices));
        
        Map<String, Object> complexEntity = new LinkedHashMap<String, Object>();
        complexEntity.put("stringValue", "test");
        complexEntity.put("listValue", new LinkedList<Object>());
        complexEntity.put("booleanValue", false);
        choices.add(complexEntity);
        assertFalse(choiceSchema.validate(choices));
        
        choices.clear();
        choices.add(complexEntity);
        assertTrue(choiceSchema.validate(choices));
    }
    
    @Test
    public void testSequenceOfChoices() {
        NeutralSchema complex = new ComplexSchema();
        
        StringSchema stringSchema = new StringSchema();
        AppInfo info = new AppInfo(null);
        info.put("required", "true");
        stringSchema.addAnnotation(info);
        
        BooleanSchema booleanSchema = new BooleanSchema();
        booleanSchema.addAnnotation(info);
        
        complex.addField("stringValue", new StringSchema());
        complex.addField("booleanValue", new BooleanSchema());
        
        choiceTypes.add(complex);
        
        choiceSchema = new ChoiceSchema(1, 1, choiceTypes);
        
        ListSchema listSchema = new ListSchema();
        listSchema.getList().add(choiceSchema);
        
        Map<String, Object> complexEntity = new LinkedHashMap<String, Object>();
        complexEntity.put("stringValue", "test");
        complexEntity.put("booleanValue", false);
        choices.add(complexEntity);
        
        List<Object> listEntity = new LinkedList<Object>();
        
        List<Object> l = new LinkedList<Object>();
        l.add(complexEntity);
        listEntity.add(l);
        l = new LinkedList<Object>();
        l.add("test object");
        listEntity.add(l);
        l = new LinkedList<Object>();
        l.add(25);
        listEntity.add(l);
        l = new LinkedList<Object>();
        l.add("test object 2");
        listEntity.add(l);

        assertTrue(listSchema.validate(listEntity));
    }
    
    @Test
    public void testInvalidChoice() {
        choiceSchema = new ChoiceSchema(1, 1, choiceTypes);
        
        choices.add(true);
        assertFalse(choiceSchema.validate(choices));
        
    }
    
}
