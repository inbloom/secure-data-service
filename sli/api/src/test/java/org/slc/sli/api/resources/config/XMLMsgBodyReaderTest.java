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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.api.representation.EntityBody;
import org.springframework.test.context.ContextConfiguration;

/**
 * Unit tests
 *
 * @author srupasinghe
 *
 */

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class XMLMsgBodyReaderTest {

    private XMLMsgBodyReader reader = new XMLMsgBodyReader();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        reader.setStaxMsgBodyReader(new StAXMsgBodyReader());
    }

    @Test
    public void testNullStream() throws IOException {
        EntityBody body = reader.readFrom(EntityBody.class, null, null, null, null, null);

        assertNotNull("Should not be null", body);
        assertTrue("Should be empty", body.isEmpty());
    }

    @Test
    public void testBadXml() throws Exception {
        try {
            InputStream is = new ByteArrayInputStream("{\"test\":\"foo\"}".getBytes());
            reader.readFrom(EntityBody.class, null, null, null, null, is);
            fail("Should throw Exception because of invalid XML format");
        } catch(WebApplicationException wae) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), wae.getResponse().getStatus());
        }
    }
}
