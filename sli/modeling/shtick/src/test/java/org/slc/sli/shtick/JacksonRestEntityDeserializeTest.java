package org.slc.sli.shtick;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

/**
 * Test GenericEntity deserialization
 */
public class JacksonRestEntityDeserializeTest {

    final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testDeserializeSimpleRestEntity() throws IOException {

        Entity e = TestHelpers.createSimpleRestEntity();
        Entity r = mapper.readValue(TestHelpers.SIMPLE_JSON, Entity.class);

        assertNotNull(r);
        assertTrue(TestHelpers.basicEntitiesEqual(e, r));
    }

    @Test
    public void testDeserializeRestEntityWithMetadata() throws IOException {

        Entity e = TestHelpers.createSimpleRestEntity();
        Entity r = mapper.readValue(TestHelpers.SIMPLE_METADATA_JSON, Entity.class);

        assertNotNull(r);
        assertTrue(TestHelpers.basicEntitiesEqual(e, r));
    }

    @Test
    public void testDeserializeComplexRestEntity() throws IOException {

        Entity e = TestHelpers.createComplexRestEntity();
        Entity r = mapper.readValue(TestHelpers.COMPLEX_JSON, Entity.class);

        assertNotNull(r);
        assertTrue(TestHelpers.basicEntitiesEqual(e, r));
    }

}
