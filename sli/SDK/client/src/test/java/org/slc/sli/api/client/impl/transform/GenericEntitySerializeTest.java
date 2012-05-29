package org.slc.sli.api.client.impl.transform;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import org.slc.sli.api.client.impl.GenericEntity;

/**
 * Test GenericEntity serialization
 */
public class GenericEntitySerializeTest {
    
    ObjectMapper mapper = new ObjectMapper();
    
    @Test
    public void testSerializeBasicEntity() throws IOException {
        
        GenericEntity e = TestHelpers.createSimpleGenericEntity();
        
        String jsonString = mapper.writeValueAsString(e);
        
        assertNotNull(jsonString);
        JsonNode eNode = mapper.readTree(jsonString);
        
        // System.err.println(TestHelpers.SIMPLE_JSON_BODY.toString());
        // System.err.println(eNode.toString());
        
        assertTrue(TestHelpers.SIMPLE_JSON_BODY.equals(eNode.toString()));
    }
    
    @Test
    public void testComplexEntity() throws IOException {
        
        GenericEntity e = TestHelpers.createComplexEntity();
        
        String jsonString = mapper.writeValueAsString(e);
        assertNotNull(jsonString);
        
        JsonNode eNode = mapper.readTree(jsonString);
        
        // System.err.println(TestHelpers.COMPLEX_JSON_BODY.toString());
        // System.err.println(eNode.toString());
        
        assertTrue(TestHelpers.COMPLEX_JSON_BODY.equals(eNode.toString()));
    }
}
