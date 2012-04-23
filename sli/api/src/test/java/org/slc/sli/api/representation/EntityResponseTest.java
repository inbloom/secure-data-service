package org.slc.sli.api.representation;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests
 *
 * @author srupasinghe
 *
 */
public class EntityResponseTest {

    @Test
    public void testEntityResponse() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("testkey", "testvalue");

        EntityResponse response = new EntityResponse("testcollection", map);
        assertNotNull("Should not be null", response.getEntity());

        Map<String, String> testMap = (Map<String, String>) response.getEntity();
        assertEquals("Should match", "testvalue", testMap.get("testkey"));
    }

    @Test
    public void testEntityResponseNullCollectionName() {
        EntityResponse response = new EntityResponse(null,  new HashMap<String, String>());
        assertNotNull("Should not be null", response.getEntity());
    }
}
