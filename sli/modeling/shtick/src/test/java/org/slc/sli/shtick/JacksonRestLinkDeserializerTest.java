package org.slc.sli.shtick;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

/**
 * Test Link deserialization
 */
public class JacksonRestLinkDeserializerTest {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testLinkDeserialize() throws IOException {
        RestLink e = TestHelpers.createBasicLink();
        RestLink r = mapper.readValue(TestHelpers.LINK_JSON, RestLink.class);

        assertNotNull(r);
        assertTrue(e.getLinkName().equals(r.getLinkName()));
        assertTrue(e.getResourceURL().equals(r.getResourceURL()));
    }
}
