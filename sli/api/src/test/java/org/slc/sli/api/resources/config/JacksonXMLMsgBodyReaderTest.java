/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
