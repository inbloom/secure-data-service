package org.slc.sli.validation.schema;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * JUnit for choice schemas
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
    public void testRequiredValue() {
        ChoiceSchema choice = new ChoiceSchema(1, 1);
        choice.addField("aString", new StringSchema());
        choice.addField("aLong", new LongSchema());
        choice.addField("aDouble", new DoubleSchema());
        
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("aString", "abc");
        assertTrue(choice.validate(data));
        
        data.put("aLong", 1L);
        assertFalse(choice.validate(data));
        
        data.remove("aString");
        assertTrue(choice.validate(data));
        
        data.put("aLong", "abc");
        assertFalse(choice.validate(data));
    }
    
    @Test
    public void testListValue() {
        ChoiceSchema choice = new ChoiceSchema(1, 5);
        choice.addField("aString", new StringSchema());
        choice.addField("aLong", new LongSchema());
        choice.addField("aDouble", new DoubleSchema());
        
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        assertFalse(choice.validate(data));
        data.add(createMap("aString", "abc"));
        assertTrue(choice.validate(data));
        
        data.add(createMap("aLong", 1L));
        assertTrue(choice.validate(data));
        
        data.add(createMap("aDouble", 1.0D));
        assertTrue(choice.validate(data));
        
        data.add(createMap("aString", "def"));
        assertTrue(choice.validate(data));
        
        data.add(createMap("invalid", "abc"));
        assertFalse(choice.validate(data));
        
        data.remove(data.size() - 1);
        data.add(createMap("aString", "hij"));
        assertTrue(choice.validate(data));
        
        data.add(createMap("aString", "hij"));
        assertFalse(choice.validate(data));
    }
    
    @Test
    public void testUnhappyPath() {
        ChoiceSchema choice = new ChoiceSchema(0, 1);
        choice.addField("aString", new StringSchema());
        choice.addField("aLong", new LongSchema());
        choice.addField("aDouble", new DoubleSchema());
        
        // range 0-1 requires a map with single entry, not wrapped in a list
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        assertFalse(choice.validate(dataList));
        dataList.add(createMap("aString", "abc"));
        assertFalse(choice.validate(dataList));
        assertTrue(choice.validate(createMap("aString", "abc")));
        Map<String, Object> invalidMap = createMap("aString", "abc");
        invalidMap.put("aLong", 1L);
        assertFalse(choice.validate(invalidMap));
        
        // range 0-N; n > 1 requires a List
        choice.setMaxOccurs(2);
        assertFalse(choice.validate(createMap("aString", "abc")));
        assertTrue(choice.validate(dataList));
        dataList.add(createMap("aString", "abc"));
        assertTrue(choice.validate(dataList));
        dataList.add(createMap("aString", "abc"));
        assertFalse(choice.validate(dataList));
    }
    
    @Test
    public void testComplexSchemas() {
        ComplexSchema c1 = new ComplexSchema();
        c1.addField("s1", new StringSchema());
        c1.addField("l1", new LongSchema());
        ComplexSchema c2 = new ComplexSchema();
        c2.addField("s2", new StringSchema());
        c2.addField("l2", new LongSchema());
        
        ChoiceSchema choice = new ChoiceSchema(0, 1);
        choice.addField("c1", c1);
        choice.addField("c2", c2);
        choice.addField("s3", new StringSchema());
        
        Map<String, Object> c1data = new HashMap<String, Object>();
        c1data.put("s1", "abc");
        c1data.put("l1", 1L);
        
        Map<String, Object> c2data = new HashMap<String, Object>();
        c2data.put("s2", "efg");
        c2data.put("l2", 1L);
        
        assertTrue(choice.validate(createMap("s3", "abc")));
        assertTrue(choice.validate(createMap("c1", c1data)));
        assertTrue(choice.validate(createMap("c2", c2data)));
        
        assertFalse(choice.validate(createMap("c2", "abc")));
        assertFalse(choice.validate(createMap("s3", c1data)));
        assertFalse(choice.validate(createMap("c1", c2data)));
    }
    
    private static Map<String, Object> createMap(String name, Object value) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(name, value);
        return map;
    }
    
    // @Test
    // public void testMinOccursZero() {
    // choiceSchema = new ChoiceSchema(0, ChoiceSchema.UNBOUNDED, null);
    //
    // // minOccurs = 0
    // assertTrue(choiceSchema.validate(choices));
    // }
    //
    // @Test
    // public void testMinOccursOne() {
    // // maxOccurs = minOccurs = 1
    // choiceSchema = new ChoiceSchema(1, 1, choiceTypes);
    //
    // choices.add("a");
    // assertTrue(choiceSchema.validate(choices));
    // }
    //
    // @Test
    // public void testMinMaxOccursTwo() {
    // // minOccurs = 2 maxOccurs = 2
    // choiceSchema = new ChoiceSchema(2, 2, choiceTypes);
    //
    // choices.add(32);
    // choices.add("b");
    // assertTrue(choiceSchema.validate(choices));
    // }
    //
    // @Test
    // public void testMinMaxOccursTwoInvalid() {
    // choiceSchema = new ChoiceSchema(2, 2, choiceTypes);
    //
    // choices.add("c");
    // assertFalse(choiceSchema.validate(choiceTypes));
    // }
    //
    // @Test
    // public void testMinOneUnboundedMax() {
    // // minOccurs = 1 maxOccurs = Unbounded.
    // choiceSchema = new ChoiceSchema(1, ChoiceSchema.UNBOUNDED, choiceTypes);
    // assertFalse(choiceSchema.validate(choiceTypes));
    //
    // choices.add("a");
    // choices.add(1);
    // choices.add("b");
    // choices.add(42);
    // assertTrue(choiceSchema.validate(choices));
    // }
    //
    // @Test
    // public void testComplexChoice() {
    // NeutralSchema complex = new ComplexSchema();
    //
    // StringSchema stringSchema = new StringSchema();
    // AppInfo info = new AppInfo(null);
    // info.put("required", "true");
    // stringSchema.addAnnotation(info);
    //
    // ListSchema listSchema = new ListSchema();
    // listSchema.addAnnotation(info);
    //
    // BooleanSchema booleanSchema = new BooleanSchema();
    // booleanSchema.addAnnotation(info);
    //
    // complex.addField("stringValue", new StringSchema());
    // complex.addField("listValue", new ListSchema());
    // complex.addField("booleanValue", new BooleanSchema());
    //
    // choiceTypes.add(complex);
    //
    // choiceSchema = new ChoiceSchema(1, 1, choiceTypes);
    // choices.add("a");
    // assertTrue(choiceSchema.validate(choices));
    //
    // choices.clear();
    // choices.add(75);
    // assertTrue(choiceSchema.validate(choices));
    //
    // Map<String, Object> complexEntity = new LinkedHashMap<String, Object>();
    // complexEntity.put("stringValue", "test");
    // complexEntity.put("listValue", new LinkedList<Object>());
    // complexEntity.put("booleanValue", false);
    // choices.add(complexEntity);
    // assertFalse(choiceSchema.validate(choices));
    //
    // choices.clear();
    // choices.add(complexEntity);
    // assertTrue(choiceSchema.validate(choices));
    // }
    //
    // @Test
    // public void testSequenceOfChoices() {
    // NeutralSchema complex = new ComplexSchema();
    //
    // StringSchema stringSchema = new StringSchema();
    // AppInfo info = new AppInfo(null);
    // info.put("required", "true");
    // stringSchema.addAnnotation(info);
    //
    // BooleanSchema booleanSchema = new BooleanSchema();
    // booleanSchema.addAnnotation(info);
    //
    // complex.addField("stringValue", new StringSchema());
    // complex.addField("booleanValue", new BooleanSchema());
    //
    // choiceTypes.add(complex);
    //
    // choiceSchema = new ChoiceSchema(1, 1, choiceTypes);
    //
    // ListSchema listSchema = new ListSchema();
    // listSchema.getList().add(choiceSchema);
    //
    // Map<String, Object> complexEntity = new LinkedHashMap<String, Object>();
    // complexEntity.put("stringValue", "test");
    // complexEntity.put("booleanValue", false);
    // choices.add(complexEntity);
    //
    // List<Object> listEntity = new LinkedList<Object>();
    //
    // List<Object> l = new LinkedList<Object>();
    // l.add(complexEntity);
    // listEntity.add(l);
    // l = new LinkedList<Object>();
    // l.add("test object");
    // listEntity.add(l);
    // l = new LinkedList<Object>();
    // l.add(25);
    // listEntity.add(l);
    // l = new LinkedList<Object>();
    // l.add("test object 2");
    // listEntity.add(l);
    //
    // assertTrue(listSchema.validate(listEntity));
    // }
    //
    // @Test
    // public void testInvalidChoice() {
    // choiceSchema = new ChoiceSchema(1, 1, choiceTypes);
    //
    // choices.add(true);
    // assertFalse(choiceSchema.validate(choices));
    //
    // }
    
}
