/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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


package org.slc.sli.api.client.impl.transform;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import org.slc.sli.api.client.impl.BasicLink;

/**
 * Test Link deserialization
 */
public class LinkDeserializerTest {
    
    ObjectMapper mapper = new ObjectMapper();
    
    @Test
    public void testLinkDeserialize() throws IOException {
        BasicLink e = TestHelpers.createBasicLink();
        BasicLink r = mapper.readValue(TestHelpers.LINK_JSON, BasicLink.class);
        
        assertNotNull(r);
        assertTrue(e.getLinkName().equals(r.getLinkName()));
        assertTrue(e.getResourceURL().equals(r.getResourceURL()));
    }
}
