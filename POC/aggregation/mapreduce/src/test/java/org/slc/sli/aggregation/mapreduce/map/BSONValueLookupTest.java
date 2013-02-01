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

package org.slc.sli.aggregation.mapreduce.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;
import org.junit.Test;

import org.slc.sli.aggregation.util.BSONUtilities;

/**
 * BSONValueLookupTest
 *
 * Extends BSONUtilities to avoid emma coverage report mis-reporting that the constructor
 * has no converage. BSONUtilities holds static helper functions only.
 */
public class BSONValueLookupTest extends BSONUtilities {

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

        assertEquals(BSONUtilities.getValue(root, "root.body.profile.name.first"), "George");
    }

    @Test
    public void testGetValues() {
        // root.body.profile.name.first = George
        BSONObject root = new BasicBSONObject();
        BSONObject body = new BasicBSONObject();
        BasicBSONList list = new BasicBSONList();

        list.add("hello");
        list.add("goodbye");
        list.add("have a nice day");

        body.put("body", list);
        root.put("root", body);

        String[] values = BSONUtilities.getValues(root,  "root.body");
        assertNotNull(values);
        assertEquals(values.length, 3);
    }

    @Test
    public void testSetValue() {

        BSONObject root = BSONUtilities.setValue("body.profile.name.first", "George");
        assertNotNull(root);
        BSONObject body = (BSONObject) root.get("body");
        assertNotNull(body);
        BSONObject profile = (BSONObject) body.get("profile");
        assertNotNull(profile);
        BSONObject name = (BSONObject) profile.get("name");
        assertNotNull(name);
        assertNotNull(name.get("first"));
        assertEquals(name.get("first"), "George");
    }
}
