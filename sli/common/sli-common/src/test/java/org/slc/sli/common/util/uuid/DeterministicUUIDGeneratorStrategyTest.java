package org.slc.sli.common.util.uuid;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import org.slc.sli.common.domain.NaturalKeyDescriptor;

/**
 * Tests for DeterministicUUIDGeneratorStrategy
 * @author ecole
 *
 */
public class DeterministicUUIDGeneratorStrategyTest {

	@Test
	public void testDeterministicUUID() {
		DeterministicUUIDGeneratorStrategy deterministicUUIDGeneratorStrategy = new DeterministicUUIDGeneratorStrategy();
        String uuid = deterministicUUIDGeneratorStrategy.deterministicUUID();
        assertNotNull(uuid);
        assertEquals('1', uuid.charAt(22)); //make sure we generated a type1 uuid
        assertEquals(43, uuid.length()); // 7 chars for 'yyyyrr-', 36 chars for type 1 uuid
	}

	@Test
	public void testDeterministicUUIDMapOfStringString() {
		Map<String, String> hashMap = new HashMap<String, String>();
    	hashMap.put("key1", "value1");
    	hashMap.put("key2", "value2");
    	hashMap.put("key3", "value3");
    	DeterministicUUIDGeneratorStrategy deterministicUUIDGeneratorStrategy = new DeterministicUUIDGeneratorStrategy();
    	NaturalKeyDescriptor naturalKeyDescriptor = new NaturalKeyDescriptor();
    	naturalKeyDescriptor.setNaturalKeys(hashMap);
    	naturalKeyDescriptor.setEntityType("testEntityType");
    	naturalKeyDescriptor.setTenantId("testTenantId");
    	String deterministicId = deterministicUUIDGeneratorStrategy.deterministicUUID(naturalKeyDescriptor);

    	assertEquals("deterministicId should be of length 36", 36, deterministicId.length());
    	assertEquals("deterministicId should be a hashId of f00d8b97-cf30-613a-d853-cbf981acf978", "f00d8b97-cf30-613a-d853-cbf981acf978", deterministicId);
	}

	@Test
	public void testGenerateUuid() {
		byte[] testBytes = "abcdefghij1234567890".getBytes();
		UUID uuid = DeterministicUUIDGeneratorStrategy.generateUuid(testBytes);
		assertNotNull("uuid must not be null", uuid);
		assertEquals("61626364-6566-6768-696a-313233343536", uuid.toString());
	}

}
