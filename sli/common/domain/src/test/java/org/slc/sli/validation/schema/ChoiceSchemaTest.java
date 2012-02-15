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
    
    NeutralSchema stringChoice = new StringSchema();
    NeutralSchema intChoice = new IntegerSchema();
    List<NeutralSchema> choiceTypes;
    
    @Before
    public void init() {
        choiceTypes = new LinkedList<NeutralSchema>();
        choiceTypes.add(stringChoice);
        choiceTypes.add(intChoice);
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
}
