package org.slc.sli.api.client.impl.transform;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import org.slc.sli.api.client.impl.GenericEntity;

public class GenericEntityDeserializeTest {

	ObjectMapper mapper = new ObjectMapper();

	@Test
	public void testDeerializeBasicEntity() throws JsonGenerationException, JsonMappingException, IOException {

		GenericEntity e = TestHelpers.createSimpleGenericEntity();
		GenericEntity r = mapper.readValue(TestHelpers.SimpleJson, GenericEntity.class);

		assertNotNull(r);
		assertTrue(TestHelpers.BasicEntitiesEqual(e, r));
	}

	@Test
	public void testSerializeBasicEntityWithMetadata() throws JsonGenerationException, JsonMappingException, IOException {

		GenericEntity e = TestHelpers.createSimpleGenericEntityWithMetadata();
		GenericEntity r = mapper.readValue(TestHelpers.SimpleMetadataJson, GenericEntity.class);

		assertNotNull(r);
		assertTrue(TestHelpers.BasicEntitiesEqual(e, r));
	}

	@Test
	public void testComplexEntity() throws JsonGenerationException, JsonMappingException, IOException {

		GenericEntity e = TestHelpers.createComplexEntity();
		GenericEntity r = mapper.readValue(TestHelpers.ComplexJson, GenericEntity.class);

		assertNotNull(r);
		assertTrue(TestHelpers.BasicEntitiesEqual(e, r));
	}

}
