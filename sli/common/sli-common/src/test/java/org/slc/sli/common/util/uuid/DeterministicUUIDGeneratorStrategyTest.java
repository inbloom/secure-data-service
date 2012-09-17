package org.slc.sli.common.util.uuid;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        
        assertEquals("deterministicId should be of length 36", 36, deterministicId.length());
        assertEquals("deterministicId should be a hashId of f00d8b97-cf30-613a-d853-cbf981acf978",
                "f00d8b97-cf30-613a-d853-cbf981acf978", deterministicId);
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
    
}
