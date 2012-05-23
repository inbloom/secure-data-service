package org.slc.sli.api.client.impl.transform;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import org.slc.sli.api.client.impl.BasicLink;

public class LinkDeserializerTest {

	ObjectMapper mapper = new ObjectMapper();

	@Test
	public void testLinkDeserialize() throws JsonParseException, JsonMappingException, IOException {
		BasicLink e = TestHelpers.createBasicLink();
		BasicLink r = mapper.readValue(TestHelpers.LinkJson, BasicLink.class);

		assertNotNull(r);
		assertTrue(e.getLinkName().equals(r.getLinkName()));
		assertTrue(e.getResourceURL().equals(r.getResourceURL()));
	}
}
