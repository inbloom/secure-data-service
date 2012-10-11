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

    @Test
    public void hashCodeShouldMatchExpected() {
        // Base values
        Map<String, String> naturalKeysForConstructor = new HashMap<String, String>();
        naturalKeysForConstructor.put("key1", "value1");
        naturalKeysForConstructor.put("key2", "value2");
        String testTenantId = "testTenantId";
        String testEntityType = "testEntityType";

        // Check basic case
        NaturalKeyDescriptor naturalKeyDescriptor = new NaturalKeyDescriptor(naturalKeysForConstructor, testTenantId, testEntityType);
        int hashCode = naturalKeyDescriptor.hashCode();
        assertEquals(-1581840760, hashCode);

        // Check that different size of naturalKeys map yields different hashCode
        Map<String, String> naturalKeysForConstructor2 = new HashMap<String, String>();
        naturalKeysForConstructor2.put("key1", "value1");
        NaturalKeyDescriptor naturalKeyDescriptor2 = new NaturalKeyDescriptor(naturalKeysForConstructor2, testTenantId, testEntityType);
        int hashCode2 = naturalKeyDescriptor2.hashCode();
        assertEquals(-2109487354, hashCode2);

        // Check that different values in naturalKeys map yields different hashCode
        Map<String, String> naturalKeysForConstructor3 = new HashMap<String, String>();
        naturalKeysForConstructor3.put("key1", "value1");
        naturalKeysForConstructor3.put("key2", "value3");
        NaturalKeyDescriptor naturalKeyDescriptor3 = new NaturalKeyDescriptor(naturalKeysForConstructor3, testTenantId, testEntityType);
        int hashCode3 = naturalKeyDescriptor3.hashCode();
        assertEquals(-1581842129, hashCode3);

        // Check that different keys in naturalKeys map yields different hashCode
        Map<String, String> naturalKeysForConstructor4 = new HashMap<String, String>();
        naturalKeysForConstructor4.put("key1", "value1");
        naturalKeysForConstructor4.put("key3", "value2");
        NaturalKeyDescriptor naturalKeyDescriptor4 = new NaturalKeyDescriptor(naturalKeysForConstructor4, testTenantId, testEntityType);
        int hashCode4 = naturalKeyDescriptor4.hashCode();
        assertEquals(-1581836653, hashCode4);

        // Check that null naturalKeys map yields different hashCode
        NaturalKeyDescriptor naturalKeyDescriptor5 = new NaturalKeyDescriptor(null, testTenantId, testEntityType);
        int hashCode5 = naturalKeyDescriptor5.hashCode();
        assertEquals(1657833348, hashCode5);

        // Check that different tenantId yields different hashCode
        NaturalKeyDescriptor naturalKeyDescriptor6 = new NaturalKeyDescriptor(naturalKeysForConstructor, "testTenantId6", testEntityType);
        int hashCode6 = naturalKeyDescriptor6.hashCode();
        assertEquals(-1964526064, hashCode6);

        // Check that null tenantId yields different hashCode
        NaturalKeyDescriptor naturalKeyDescriptor7 = new NaturalKeyDescriptor(naturalKeysForConstructor, null, testEntityType);
        int hashCode7 = naturalKeyDescriptor7.hashCode();
        assertEquals(-1139587787, hashCode7);

        // Check that different entityType yields different hashCode
        NaturalKeyDescriptor naturalKeyDescriptor8 = new NaturalKeyDescriptor(naturalKeysForConstructor, testTenantId, "testEntityType8");
        int hashCode8 = naturalKeyDescriptor8.hashCode();
        assertEquals(1227384002, hashCode8);

        // Check that null tenantId yields different hashCode
        NaturalKeyDescriptor naturalKeyDescriptor9 = new NaturalKeyDescriptor(naturalKeysForConstructor, testTenantId, null);
        int hashCode9 = naturalKeyDescriptor9.hashCode();
        assertEquals(615167641, hashCode9);
    }

}
