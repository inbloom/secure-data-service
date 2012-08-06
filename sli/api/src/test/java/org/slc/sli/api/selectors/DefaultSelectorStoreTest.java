package org.slc.sli.api.selectors;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class DefaultSelectorStoreTest {
    
    @Test
    public void testSuccessfulLoadOfData() {

        DefaultSelectorStore defaultSelectorStore = new DefaultSelectorStore();
        
        Map<String, Object> expectedType1ResultsMap = new HashMap<String, Object>();
        expectedType1ResultsMap.put("*", true);
        
        Map<String, Object> expectedType2ResultsMap = new HashMap<String, Object>();
        expectedType2ResultsMap.put("someField", true);

        Map<String, Object> expectedType3ResultsMap = null;
        
//        Map<String, Object> actualType1ResultsMap = defaultSelectorStore.getSelector("type1");
//        Map<String, Object> actualType2ResultsMap = defaultSelectorStore.getSelector("type2");
//        Map<String, Object> actualType3ResultsMap = defaultSelectorStore.getSelector("type3");
//
//        assertEquals(expectedType1ResultsMap, actualType1ResultsMap);
//        assertEquals(expectedType2ResultsMap, actualType2ResultsMap);
//        assertEquals(expectedType3ResultsMap, actualType3ResultsMap);
    }
    
    @Test
    public void assertGracefulHandlingOfMissingFile() {
//        DefaultSelectorStore defaultSelectorStore = new DefaultSelectorStore("this file does not exist");
//        assertEquals(defaultSelectorStore.getSelector("type1"), null);
    }
    
    @Test
    public void assertGracefulHandlingOfInvalidDefaultSelectorFile1() {
//        DefaultSelectorStore defaultSelectorStore = new DefaultSelectorStore("/config/invalidDefaultSelectors1.json");
//        assertEquals(defaultSelectorStore.getSelector("type1"), null);
    }
    
    @Test
    public void assertGracefulHandlingOfInvalidDefaultSelectorFile2() {
//        DefaultSelectorStore defaultSelectorStore = new DefaultSelectorStore("/config/invalidDefaultSelectors2.json");
//        assertEquals(defaultSelectorStore.getSelector("type1"), null);
    }
    
    @Test
    public void testSomewhatSuccessfulLoadOfData() {

//        DefaultSelectorStore defaultSelectorStore = new DefaultSelectorStore("/config/somewhatValidDefaultSelectors.json");
//
//        Map<String, Object> expectedType1ResultsMap = new HashMap<String, Object>();
//        Map<String, Object> expectedType2ResultsMap = null;
//        Map<String, Object> expectedType3ResultsMap = new HashMap<String, Object>();
//        Map<String, Object> expectedType4ResultsMap = null;
//        Map<String, Object> expectedType5ResultsMap = null;
//
//        Map<String, Object> actualType1ResultsMap = defaultSelectorStore.getSelector("type1");
//        Map<String, Object> actualType2ResultsMap = defaultSelectorStore.getSelector("type2");
//        Map<String, Object> actualType3ResultsMap = defaultSelectorStore.getSelector("type3");
//        Map<String, Object> actualType4ResultsMap = defaultSelectorStore.getSelector("type4");
//        Map<String, Object> actualType5ResultsMap = defaultSelectorStore.getSelector("type5");
//
//        assertEquals(expectedType1ResultsMap, actualType1ResultsMap);
//        assertEquals(expectedType2ResultsMap, actualType2ResultsMap);
//        assertEquals(expectedType3ResultsMap, actualType3ResultsMap);
//        assertEquals(expectedType4ResultsMap, actualType4ResultsMap);
//        assertEquals(expectedType5ResultsMap, actualType5ResultsMap);
    }
    
}
