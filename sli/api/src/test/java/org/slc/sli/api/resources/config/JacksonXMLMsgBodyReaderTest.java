package org.slc.sli.api.resources.config;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.api.representation.EntityBody;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests
 *
 * @author srupasinghe
 *
 */
public class JacksonXMLMsgBodyReaderTest {
    private JacksonXMLMsgBodyReader reader;

    @Before
    public void setup() {
        reader = new JacksonXMLMsgBodyReader();
    }

    @Test
    public void testEntityStream() throws IOException {
        String xmlString = "<teacher><name><first>Test</first><last>User</last></name></teacher>";
        ByteArrayInputStream xmlStream = new ByteArrayInputStream(xmlString.getBytes());

        EntityBody body = reader.readFrom(EntityBody.class, null, null, null, null, xmlStream);

        assertNotNull("Should not be null", body);
        Map<String, Object> nameNody = (Map<String, Object>) body.get("name");
        assertNotNull("Should not be null", nameNody);
        assertEquals("Should match", "Test", nameNody.get("first"));
    }

    @Test
    public void testNullStream() throws IOException {
        EntityBody body = reader.readFrom(EntityBody.class, null, null, null, null, null);

        assertNotNull("Should not be null", body);
        assertTrue("Should be empty", body.isEmpty());
    }

    @Test(expected = IOException.class)
    public void testEmptyEntityStream() throws IOException {
        String xmlString = "";
        ByteArrayInputStream xmlStream = new ByteArrayInputStream(xmlString.getBytes());

        EntityBody body = reader.readFrom(EntityBody.class, null, null, null, null, xmlStream);
    }
}
