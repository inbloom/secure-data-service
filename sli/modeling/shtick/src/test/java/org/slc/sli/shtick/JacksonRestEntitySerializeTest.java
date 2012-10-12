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
