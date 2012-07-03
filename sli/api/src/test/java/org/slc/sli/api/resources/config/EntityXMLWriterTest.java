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
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.representation.ErrorResponse;
import org.slc.sli.api.representation.Home;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for EntityWriter
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class EntityXMLWriterTest {
    @InjectMocks
    private EntityXMLWriter writer = new EntityXMLWriter();

    @Mock
    private EntityDefinitionStore entityDefinitionStore;

    @Before
    public void setup() {
    }

    @Test
    public void testEntityBody() throws IOException {
        EntityDefinition def = mock(EntityDefinition.class);
        when(def.getResourceName()).thenReturn("TestResource");
        when(entityDefinitionStore.lookupByEntityType(anyString())).thenReturn(def);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EntityBody body = new EntityBody();

        body.put("id", "1234");
        body.put("name", "test name");

        EntityResponse response = new EntityResponse("TestEntity", body);
        writer.writeTo(response, null, null, null, null, null, out);

        assertNotNull("Should not be null", out);

        String value = new String(out.toByteArray());
        assertTrue("Should match", value.indexOf("<TestResource>") > 0);
        assertTrue("Should match", value.indexOf("<id>") > 0);
        assertTrue("Should match", value.indexOf("<name>") > 0);
    }

    @Test
    public void testEntityNullCollection() throws IOException {
        EntityDefinition def = mock(EntityDefinition.class);
        when(def.getResourceName()).thenReturn("EntityResource");
        when(entityDefinitionStore.lookupByEntityType(anyString())).thenReturn(def);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EntityBody body = new EntityBody();
        body.put("id", "1234");
        body.put("name", "test name");

        EntityResponse response = new EntityResponse(null, body);
        writer.writeTo(response, null, null, null, null, null, out);

        assertNotNull("Should not be null", out);

        String value = new String(out.toByteArray());
        assertTrue("Should match", value.indexOf("<EntityResource>") > 0);
    }

    @Test
    public void testIsWritable() {
        assertFalse(writer.isWriteable(ErrorResponse.class, null, null, null));
        assertFalse(writer.isWriteable(Home.class, null, null, null));
        assertTrue(writer.isWriteable(EntityResponse.class, null, null, null));
    }
}
