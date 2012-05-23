package org.slc.sli.api.client.impl.transform;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import org.slc.sli.api.client.impl.GenericEntity;

public class GenericEntitySerializeTest {

	ObjectMapper mapper = new ObjectMapper();

	@Test
	public void testSerializeBasicEntity() throws JsonGenerationException, JsonMappingException, IOException {

		GenericEntity e = TestHelpers.createSimpleGenericEntity();

		String jsonString = mapper.writeValueAsString(e);

		assertNotNull(jsonString);
		JsonNode eNode = mapper.readTree(jsonString);

		//System.err.println(TestHelpers.SimpleJsonObject.toString());
		//System.err.println(eNode.toString());

		assertTrue(TestHelpers.SimpleJsonObject.equals(eNode));
	}

	@Test
	public void testSerializeBasicEntityWithMetadata() throws JsonGenerationException, JsonMappingException, IOException {

		GenericEntity e = TestHelpers.createSimpleGenericEntityWithMetadata();

		String jsonString = mapper.writeValueAsString(e);
		assertNotNull(jsonString);

		JsonNode eNode = mapper.readTree(jsonString);

		//System.err.println(TestHelpers.SimpleMetadataJsonObject.toString());
		//System.err.println(eNode.toString());

		assertTrue(TestHelpers.SimpleMetadataJsonObject.equals(eNode));
	}

	@Test
	public void testComplexEntity() throws JsonGenerationException, JsonMappingException, IOException {

		GenericEntity e = TestHelpers.createComplexEntity();

		String jsonString = mapper.writeValueAsString(e);
		assertNotNull(jsonString);

		JsonNode eNode = mapper.readTree(jsonString);

		//System.err.println(TestHelpers.ComplexJsonObject.toString());
		//System.err.println(eNode.toString());

		assertTrue(TestHelpers.ComplexJsonObject.equals(eNode));

		//System.err.println(TestHelpers.ComplexJson);
		//System.err.println(jsonString);
	}
}
