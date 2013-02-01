/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

package org.slc.sli.common.util.uuid;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.common.domain.NaturalKeyDescriptor;

/**
 * Tests for DeterministicUUIDGeneratorStrategy
 *
 * @author ecole
 *
 */
public class DeterministicUUIDGeneratorStrategyTest {

    @InjectMocks
    DeterministicUUIDGeneratorStrategy deterministicUUIDGeneratorStrategy = new DeterministicUUIDGeneratorStrategy();

    @Mock
    ShardType1UUIDGeneratorStrategy mockShardType1UUIDGeneratorStrategy;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDeterministicUUID() {

        String resultUuid = "someId";

        Mockito.when(mockShardType1UUIDGeneratorStrategy.generateId()).thenReturn(resultUuid);

        String uuid = deterministicUUIDGeneratorStrategy.generateId();
        assertNotNull(uuid);
        assertEquals(resultUuid, uuid); // make sure we generated a type1 uuid
    }

    @Test
    public void shouldHaveSuffix() {
        String expectedSuffix = "_id";

        Map<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key1", "value1");
        NaturalKeyDescriptor naturalKeyDescriptor = new NaturalKeyDescriptor();
        naturalKeyDescriptor.setNaturalKeys(hashMap);
        naturalKeyDescriptor.setEntityType("testEntityType");
        naturalKeyDescriptor.setTenantId("testTenantId");
        String deterministicId = deterministicUUIDGeneratorStrategy.generateId(naturalKeyDescriptor);

        boolean hasSuffix = deterministicId.endsWith(expectedSuffix);
        Assert.assertTrue("Incorrect suffix: " + deterministicId, hasSuffix);
    }

    @Test
    public void testDeterministicUUIDMapOfStringString() {
        Map<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key1", "value1");
        hashMap.put("key2", "value2");
        hashMap.put("key3", "value3");
        NaturalKeyDescriptor naturalKeyDescriptor = new NaturalKeyDescriptor();
        naturalKeyDescriptor.setNaturalKeys(hashMap);
        naturalKeyDescriptor.setEntityType("testEntityType");
        naturalKeyDescriptor.setTenantId("testTenantId");
        String deterministicId = deterministicUUIDGeneratorStrategy.generateId(naturalKeyDescriptor);

        assertEquals("deterministicId should be of length 43", 43, deterministicId.length());
        assertEquals("deterministicId should be a specific value", "a9e40c18e0638c2ef986deca9dec43ccc26349f4_id",
                deterministicId);
    }

    @Test
    public void testNullNaturalKeyDescriptor() {
        String resultUuid = "someId";

        Mockito.when(mockShardType1UUIDGeneratorStrategy.generateId()).thenReturn(resultUuid);

        String uuid = deterministicUUIDGeneratorStrategy.generateId(null);
        assertNotNull(uuid);
        assertEquals(resultUuid, uuid); // make sure we generated a type1 uuid
    }

    @Test
    public void testGenerateUuid() {
        byte[] testBytes = "abcdefghij1234567890".getBytes();
        UUID uuid = DeterministicUUIDGeneratorStrategy.generateUuid(testBytes);
        assertNotNull("uuid must not be null", uuid);
        assertEquals("61626364-6566-6768-696a-313233343536", uuid.toString());
    }

    @Test
    public void testDeterministicUUIDMapOfStringString2() {
        Map<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key1", "value1");
        hashMap.put("key2", "value2");
        hashMap.put("key3", "value3");
        NaturalKeyDescriptor naturalKeyDescriptor = new NaturalKeyDescriptor();
        naturalKeyDescriptor.setNaturalKeys(hashMap);
        naturalKeyDescriptor.setEntityType("entity||");
        naturalKeyDescriptor.setTenantId("Type");
        String deterministicId = deterministicUUIDGeneratorStrategy.generateId(naturalKeyDescriptor);

        NaturalKeyDescriptor naturalKeyDescriptor2 = new NaturalKeyDescriptor();
        naturalKeyDescriptor2.setNaturalKeys(hashMap);
        naturalKeyDescriptor2.setEntityType("entity");
        naturalKeyDescriptor2.setTenantId("||Type");
        String deterministicId2 = deterministicUUIDGeneratorStrategy.generateId(naturalKeyDescriptor2);

        Assert.assertFalse("Ids should not be the same: ", deterministicId.equals(deterministicId2));
    }

    @Test
    public void testWithBothDelimiters() {
        Map<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key1", "value1");
        NaturalKeyDescriptor naturalKeyDescriptor = new NaturalKeyDescriptor();
        naturalKeyDescriptor.setNaturalKeys(hashMap);
        naturalKeyDescriptor.setEntityType("entity|~");
        naturalKeyDescriptor.setTenantId("Type");
        String deterministicId = deterministicUUIDGeneratorStrategy.generateId(naturalKeyDescriptor);

        NaturalKeyDescriptor naturalKeyDescriptor2 = new NaturalKeyDescriptor();
        naturalKeyDescriptor2.setNaturalKeys(hashMap);
        naturalKeyDescriptor2.setEntityType("entity");
        naturalKeyDescriptor2.setTenantId("|~Type");
        String deterministicId2 = deterministicUUIDGeneratorStrategy.generateId(naturalKeyDescriptor2);

        Assert.assertFalse("Ids should not be the same: ", deterministicId.equals(deterministicId2));
    }

    @Test
    public void testWithSecondDelimiter() {
        Map<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key1", "value1");
        NaturalKeyDescriptor naturalKeyDescriptor = new NaturalKeyDescriptor();
        naturalKeyDescriptor.setNaturalKeys(hashMap);
        naturalKeyDescriptor.setEntityType("entity~");
        naturalKeyDescriptor.setTenantId("Type");
        String deterministicId = deterministicUUIDGeneratorStrategy.generateId(naturalKeyDescriptor);

        NaturalKeyDescriptor naturalKeyDescriptor2 = new NaturalKeyDescriptor();
        naturalKeyDescriptor2.setNaturalKeys(hashMap);
        naturalKeyDescriptor2.setEntityType("entity");
        naturalKeyDescriptor2.setTenantId("~Type");
        String deterministicId2 = deterministicUUIDGeneratorStrategy.generateId(naturalKeyDescriptor2);

        Assert.assertFalse("Ids should not be the same: ", deterministicId.equals(deterministicId2));
    }


}
