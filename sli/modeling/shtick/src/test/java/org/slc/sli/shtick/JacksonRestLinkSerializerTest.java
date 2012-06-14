package org.slc.sli.shtick;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

/**
 * Test Link serialization
 */
public class JacksonRestLinkSerializerTest {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testLinkSerialize() throws IOException {

        final RestLink e = TestHelpers.createBasicLink();

        final String jsonString = mapper.writeValueAsString(e);

        assertNotNull(jsonString);
        final JsonNode node = mapper.readTree(jsonString);

        // System.err.println(TestHelpers.LinkJsonObject.toString());
        // System.err.println(eNode.toString());

        assertTrue(TestHelpers.LINK_JSON_OBJECT.equals(node));
    }

}
