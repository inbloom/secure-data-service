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


package org.slc.sli.api.client.impl.transform;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import org.slc.sli.api.client.impl.GenericEntity;

/**
 * Test GenericEntity serialization
 */
public class GenericEntitySerializeTest {
    
    ObjectMapper mapper = new ObjectMapper();
    
    @Test
    public void testSerializeBasicEntity() throws IOException {
        
        GenericEntity e = TestHelpers.createSimpleGenericEntity();
        
        String jsonString = mapper.writeValueAsString(e);
        
        assertNotNull(jsonString);
        JsonNode eNode = mapper.readTree(jsonString);
        
        // System.err.println(TestHelpers.SIMPLE_JSON_BODY.toString());
        // System.err.println(eNode.toString());
        
        assertTrue(TestHelpers.SIMPLE_JSON_BODY.equals(eNode.toString()));
    }
    
    @Test
    public void testComplexEntity() throws IOException {
        
        GenericEntity e = TestHelpers.createComplexEntity();
        
        String jsonString = mapper.writeValueAsString(e);
        assertNotNull(jsonString);
        
        JsonNode eNode = mapper.readTree(jsonString);
        
        // System.err.println(TestHelpers.COMPLEX_JSON_BODY.toString());
        // System.err.println(eNode.toString());
        
        assertTrue(TestHelpers.COMPLEX_JSON_BODY.equals(eNode.toString()));
    }
}
