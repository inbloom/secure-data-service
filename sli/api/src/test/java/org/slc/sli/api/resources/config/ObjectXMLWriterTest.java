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
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.representation.ErrorResponse;
import org.slc.sli.api.representation.Home;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerationException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests
 *
 * @author srupasinghe
 *
 */
public class ObjectXMLWriterTest {
    private ObjectXMLWriter writer;

    @Before
    public void setup() {
        writer = new ObjectXMLWriter();
    }

    @Test(expected = JsonGenerationException.class)
    public void testEntityNullCollection() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EntityBody body = new EntityBody();
        body.put("id", "1234");
        body.put("name", "test name");

        Home response = new Home(null, body);
        writer.writeTo(response, null, null, null, null, null, out);
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

    @Test(expected = IllegalArgumentException.class)
    public void testNullObject() throws IOException {
        EntityBody body = new EntityBody();

        body.put("id", "1234");
        body.put("name", "test name");

        Home response = new Home("TestEntity", body);
        writer.writeTo(response, null, null, null, null, null, null);
    }

    @Test
    public void testIsWritable() {
        assertTrue(writer.isWriteable(ErrorResponse.class, null, null, null));
        assertTrue(writer.isWriteable(Home.class, null, null, null));
        assertFalse(writer.isWriteable(EntityResponse.class, null, null, null));
    }
}
