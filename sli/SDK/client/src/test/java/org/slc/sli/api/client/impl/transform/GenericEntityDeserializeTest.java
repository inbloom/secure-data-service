package org.slc.sli.api.client.impl.transform;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import org.slc.sli.api.client.impl.GenericEntity;

/**
 * Test GenericEntity deserialization
 */
public class GenericEntityDeserializeTest {
    
    ObjectMapper mapper = new ObjectMapper();
    
    @Test
    public void testDeserializeBasicEntity() throws IOException {
        
        GenericEntity e = TestHelpers.createSimpleGenericEntity();
        GenericEntity r = mapper.readValue(TestHelpers.SIMPLE_JSON, GenericEntity.class);
        
        assertNotNull(r);
        assertTrue(TestHelpers.basicEntitiesEqual(e, r));
    }
    
    @Test
    public void testDeserializeBasicEntityWithMetadata() throws IOException {
        
        GenericEntity e = TestHelpers.createSimpleGenericEntity();
        GenericEntity r = mapper.readValue(TestHelpers.SIMPLE_METADATA_JSON, GenericEntity.class);
        
        assertNotNull(r);
        assertTrue(TestHelpers.basicEntitiesEqual(e, r));
    }
    
    @Test
    public void testDeserializeComplexEntity() throws IOException {
        
        GenericEntity e = TestHelpers.createComplexEntity();
        GenericEntity r = mapper.readValue(TestHelpers.COMPLEX_JSON, GenericEntity.class);
        
        assertNotNull(r);
        assertTrue(TestHelpers.basicEntitiesEqual(e, r));
    }
    
}
