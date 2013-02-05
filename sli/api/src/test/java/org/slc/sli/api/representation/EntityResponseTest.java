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


package org.slc.sli.api.representation;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests
 *
 * @author srupasinghe
 *
 */
public class EntityResponseTest {

    @Test
    public void testEntityResponse() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("testkey", "testvalue");

        EntityResponse response = new EntityResponse("testcollection", map);
        assertNotNull("Should not be null", response.getEntity());

        Map<String, String> testMap = (Map<String, String>) response.getEntity();
        assertEquals("Should match", "testvalue", testMap.get("testkey"));
    }

    @Test
    public void testEntityResponseNullCollectionName() {
        EntityResponse response = new EntityResponse(null,  new HashMap<String, String>());
        assertNotNull("Should not be null", response.getEntity());
    }
}
