/**
 *
 */
package org.slc.sli.common.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @author ecole
 *
 */
public class NaturalKeyDescriptorTest {

    /**
     * Test method for {@link org.slc.sli.common.domain.NaturalKeyDescriptor#NaturalKeyDescriptor()}.
     */
    @Test
    public void testNaturalKeyDescriptor() {
        NaturalKeyDescriptor naturalKeyDescriptor = new NaturalKeyDescriptor();
        Map<String, String> naturalKeys = naturalKeyDescriptor.getNaturalKeys();
        assertNotNull("naturalKeys is not null", naturalKeys);
        assertEquals("naturalKeys has 0 natural keys", 0, naturalKeys.size());
        assertEquals("tenantId is empty", "", naturalKeyDescriptor.getTenantId());
        assertEquals("entityType is empty", "", naturalKeyDescriptor.getEntityType());
        NaturalKeyDescriptor naturalKeyDescriptor2 = new NaturalKeyDescriptor();
        assertTrue(naturalKeyDescriptor.equals(naturalKeyDescriptor2));
    }

    /**
     * Test method for {@link org.slc.sli.common.domain.NaturalKeyDescriptor#NaturalKeyDescriptor(java.util.Map)}.
     */
    @Test
    public void testNaturalKeyDescriptorMapOfStringString() {
        Map<String, String> naturalKeysForConstructor = new HashMap<String, String>();
        naturalKeysForConstructor.put("key1", "value1");
        naturalKeysForConstructor.put("key2", "value2");
        naturalKeysForConstructor.put("key3", "value3");
        NaturalKeyDescriptor naturalKeyDescriptor = new NaturalKeyDescriptor(naturalKeysForConstructor);
        Map<String, String> naturalKeys = naturalKeyDescriptor.getNaturalKeys();
        assertNotNull("naturalKeys is not null", naturalKeys);
        assertEquals("naturalKeys has 3 natural keys", 3, naturalKeys.size());
        assertEquals("naturalKeys key1 equals value1", "value1", naturalKeys.get("key1"));
        assertEquals("tenantId is empty", "", naturalKeyDescriptor.getTenantId());
        assertEquals("entityType is empty", "", naturalKeyDescriptor.getEntityType());
        NaturalKeyDescriptor naturalKeyDescriptor2 = new NaturalKeyDescriptor();
        assertTrue(!naturalKeyDescriptor.equals(naturalKeyDescriptor2));
        Map<String, String> naturalKeysForConstructor2 = new HashMap<String, String>();
        naturalKeysForConstructor2.put("key1", "value1");
        naturalKeysForConstructor2.put("key2", "value2");
        naturalKeysForConstructor2.put("key3", "value3");
        NaturalKeyDescriptor naturalKeyDescriptor3 = new NaturalKeyDescriptor(naturalKeysForConstructor2);
        assertTrue(naturalKeyDescriptor.equals(naturalKeyDescriptor3));
    }

    /**
     * Test method for {@link org.slc.sli.common.domain.NaturalKeyDescriptor#NaturalKeyDescriptor(java.util.Map, java.lang.String, java.lang.String)}.
     */
    @Test
    public void testNaturalKeyDescriptorMapOfStringStringStringString() {
        Map<String, String> naturalKeysForConstructor = new HashMap<String, String>();
        naturalKeysForConstructor.put("key1", "value1");
        naturalKeysForConstructor.put("key2", "value2");
        String testTenantId = "testTenantId";
        String testEntityType = "testEntityType";
        NaturalKeyDescriptor naturalKeyDescriptor = new NaturalKeyDescriptor(naturalKeysForConstructor, testTenantId, testEntityType);
        Map<String, String> naturalKeys = naturalKeyDescriptor.getNaturalKeys();
        assertNotNull("naturalKeys is not null", naturalKeys);
        assertEquals("naturalKeys has 2 natural keys", 2, naturalKeys.size());
        assertEquals("naturalKeys key1 equals value1", "value1", naturalKeys.get("key1"));
        assertEquals("tenantId is empty", testTenantId, naturalKeyDescriptor.getTenantId());
        assertEquals("entityType is empty", testEntityType, naturalKeyDescriptor.getEntityType());
        NaturalKeyDescriptor naturalKeyDescriptor2 = new NaturalKeyDescriptor();
        assertTrue(!naturalKeyDescriptor.equals(naturalKeyDescriptor2));
        Map<String, String> naturalKeysForConstructor2 = new HashMap<String, String>();
        naturalKeysForConstructor2.put("key1", "value1");
        naturalKeysForConstructor2.put("key2", "value2");
        NaturalKeyDescriptor naturalKeyDescriptor3 = new NaturalKeyDescriptor(naturalKeysForConstructor2, "testTenantId", "testEntityType");
        assertTrue(naturalKeyDescriptor.equals(naturalKeyDescriptor3));
    }

}
