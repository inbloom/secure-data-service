package org.slc.sli.shtick;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Ignore;

/**
 * Test GenericEntity serialization
 */
public class JacksonRestEntitySerializeTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Ignore("FIXME: Map ordering prevents comparison at string level.")
    public void testSerializeSimpleRestEntity() throws IOException {

        final Entity e = TestHelpers.createSimpleRestEntity();

        final String jsonString = mapper.writeValueAsString(e);
        assertNotNull(jsonString);

        final JsonNode node = mapper.readTree(jsonString);

        // System.err.println(TestHelpers.SIMPLE_JSON_BODY.toString());
        // System.err.println(eNode.toString());

        // This fails because Map does not preserve order.
        assertEquals(TestHelpers.SIMPLE_JSON_BODY, node.toString());
    }

    @Ignore("FIXME: Map ordering prevents comparison at string level.")
    public void testSerializeComplexRestEntity() throws IOException {

        final Entity e = TestHelpers.createComplexRestEntity();

        final String jsonString = mapper.writeValueAsString(e);
        assertNotNull(jsonString);

        final JsonNode node = mapper.readTree(jsonString);

        // System.err.println(TestHelpers.COMPLEX_JSON_BODY.toString());
        // System.err.println(eNode.toString());

        assertTrue(TestHelpers.COMPLEX_JSON_BODY.equals(node.toString()));
    }
}
