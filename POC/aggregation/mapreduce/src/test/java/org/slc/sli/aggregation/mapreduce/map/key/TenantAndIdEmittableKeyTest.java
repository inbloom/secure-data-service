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

package org.slc.sli.aggregation.mapreduce.map.key;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.hadoop.io.Text;
import org.bson.BSONObject;
import org.junit.Test;

/**
 * TenantAndIdEmittableKeyTest
 */
public class TenantAndIdEmittableKeyTest {

    @Test
    public void testToBSON() {
        TenantAndIdEmittableKey key = new TenantAndIdEmittableKey("meta.data.tenantId", "test.id.key.field");
        key.setTenantId(new Text("Midgar"));
        key.setId(new Text("1234"));

        BSONObject bson = key.toBSON();
        assertNotNull(bson);
        assertTrue(bson.containsField("meta.data.tenantId"));

        Object obj = bson.get("meta.data.tenantId");
        assertNotNull(obj);
        assertTrue(obj instanceof String);
        String val = (String) obj;
        assertEquals(val, "Midgar");

        assertTrue(bson.containsField("test.id.key.field"));
        obj = bson.get("test.id.key.field");
        assertNotNull(obj);
        assertTrue(obj instanceof String);
        val = (String) obj;
        assertEquals(val, "1234");
    }

    @Test
    public void testGetTenantIdField() {
        TenantAndIdEmittableKey key = new TenantAndIdEmittableKey("meta.data.tenantId", "test.id.key.field");
        assertEquals(key.getTenantIdField().toString(), "meta.data.tenantId");
    }

    @Test
    public void testGetIdField() {
        TenantAndIdEmittableKey key = new TenantAndIdEmittableKey("meta.data.tenantId", "test.id.key.field");
        assertEquals(key.getIdField().toString(), "test.id.key.field");
    }

    @Test
    public void testSetGetTenantId() {
        TenantAndIdEmittableKey key = new TenantAndIdEmittableKey("meta.data.tenantId", "test.id.key.field");
        key.setTenantId(new Text("Midgar"));

        Text tenantId = key.getTenantId();
        assertEquals(tenantId.toString(), "Midgar");
    }

    @Test
    public void testSetGetId() {
        TenantAndIdEmittableKey key = new TenantAndIdEmittableKey("meta.data.tenantId", "test.id.key.field");
        key.setId(new Text("1234"));

        Text id = key.getId();
        assertEquals(id.toString(), "1234");
    }

    @Test
    public void testToString() {
        TenantAndIdEmittableKey key = new TenantAndIdEmittableKey("meta.data.tenantId", "test.id.key.field");
        key.setTenantId(new Text("Midgar"));
        key.setId(new Text("1234"));

        assertEquals(key.toString(), "TenantAndIdEmittableKey [test.id.key.field=1234, meta.data.tenantId=Midgar]");
    }
}
