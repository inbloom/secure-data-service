package org.slc.sli.api.resources.config;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.representation.Home;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests
 *
 * @author srupasinghe
 *
 */
public class JacksonXMLMsgBodyWriterTest {
    private JacksonXMLMsgBodyWriter writer;
    private DocumentBuilderFactory docFactory;

    @Before
    public void setup() {
        docFactory = DocumentBuilderFactory.newInstance();
        writer = new JacksonXMLMsgBodyWriter();

        docFactory.setNamespaceAware(true);
    }

    @Test
    public void testEntityBody() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EntityBody body = new EntityBody();

        body.put("id", "1234");
        body.put("name", "test name");

        EntityResponse response = new EntityResponse("TestEntity", body);
        writer.writeTo(response, null, null, null, null, null, out);

        assertNotNull("Should not be null", out);

        String value = new String(out.toByteArray());
        assertTrue("Should match", value.startsWith("<EntityResponse"));
        assertTrue("Should match", value.indexOf("<TestEntity>") > 0);
        assertTrue("Should match", value.indexOf("<id>") > 0);
        assertTrue("Should match", value.indexOf("<name>") > 0);
    }

    @Test
    public void testEntityNullCollection() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EntityBody body = new EntityBody();
        body.put("id", "1234");
        body.put("name", "test name");

        EntityResponse response = new EntityResponse(null, body);
        writer.writeTo(response, null, null, null, null, null, out);

        assertNotNull("Should not be null", out);

        String value = new String(out.toByteArray());
        assertTrue("Should match", value.startsWith("<EntityResponse"));
        assertTrue("Should match", value.indexOf("<Entity>") > 0);
    }

    @Test
    public void testEntityHome() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EntityBody body = new EntityBody();

        body.put("id", "1234");
        body.put("name", "test name");

        Home response = new Home("TestEntity", body);
        writer.writeTo(response, null, null, null, null, null, out);

        assertNotNull("Should not be null", out);

        String value = new String(out.toByteArray());
        assertTrue("Should match", value.startsWith("<Home"));
        assertTrue("Should match", value.indexOf("<id>") > 0);
        assertTrue("Should match", value.indexOf("<name>") > 0);
    }

    @Test(expected = IOException.class)
    public void testNullObject() throws IOException {
        EntityBody body = new EntityBody();

        body.put("id", "1234");
        body.put("name", "test name");

        Home response = new Home("TestEntity", body);
        writer.writeTo(response, null, null, null, null, null, null);
    }
}
