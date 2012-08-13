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

package org.slc.sli.aggregation.mapreduce.map;

import static org.junit.Assert.assertEquals;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.junit.Test;

/**
 * BSONValueLookupTest
 * 
 * Extends BSONValueLookup to avoid emma coverage report mis-reporting that the constructor
 * has no converage. BSONValueLookup holds static helper functions only.
 */
public class BSONValueLookupTest extends BSONValueLookup {
    
    @Test
    public void testGetValue() {
        // root.body.profile.name.first = George
        BSONObject root = new BasicBSONObject();
        BSONObject body = new BasicBSONObject();
        BSONObject profile = new BasicBSONObject();
        BSONObject name = new BasicBSONObject();
        BSONObject first = new BasicBSONObject();
        
        first.put("first", "George");
        name.put("name", first);
        profile.put("profile", name);
        body.put("body", profile);
        root.put("root", body);
        
        assertEquals(BSONValueLookup.getValue(root, "root.body.profile.name.first"), "George");
    }
}
