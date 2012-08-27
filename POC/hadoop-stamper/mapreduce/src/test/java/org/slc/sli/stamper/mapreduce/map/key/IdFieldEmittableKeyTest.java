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

package org.slc.sli.stamper.mapreduce.map.key;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.bson.BSONObject;
import org.junit.Test;

/**
 * IdFieldEmittableKeyTest
 */
public class IdFieldEmittableKeyTest {

    @Test
    public void testToBSON() {
        org.slc.sli.stamper.mapreduce.map.key.IdFieldEmittableKey key = new org.slc.sli.stamper.mapreduce.map.key.IdFieldEmittableKey("test.id.key.field");
        key.setId(new Text("1234"));

        BSONObject bson = key.toBSON();
        assertNotNull(bson);
        assertTrue(bson.containsField("test.id.key.field"));

        Object obj = bson.get("test.id.key.field");
        assertNotNull(obj);
        assertTrue(obj instanceof String);
        String val = (String) obj;
        assertEquals(val, "1234");
    }

    @Test
    public void testGetIdField() {
        org.slc.sli.stamper.mapreduce.map.key.IdFieldEmittableKey key = new org.slc.sli.stamper.mapreduce.map.key.IdFieldEmittableKey("test.id.key.field");
        assertEquals(key.getIdField().toString(), "test.id.key.field");
    }

    @Test
    public void testSetGetId() {
        org.slc.sli.stamper.mapreduce.map.key.IdFieldEmittableKey key = new org.slc.sli.stamper.mapreduce.map.key.IdFieldEmittableKey("test.id.key.field");
        key.setId(new Text("1234"));

        Text id = key.getId();
        assertEquals(id.toString(), "1234");
    }

    @Test
    public void testToString() {
        org.slc.sli.stamper.mapreduce.map.key.IdFieldEmittableKey key = new org.slc.sli.stamper.mapreduce.map.key.IdFieldEmittableKey("test.id.key.field");
        key.setId(new Text("1234"));

        assertEquals(key.toString(), "IdFieldEmittableKey [test.id.key.field=1234]");
    }

    @Test
    public void testHash() {
        org.slc.sli.stamper.mapreduce.map.key.IdFieldEmittableKey key = new org.slc.sli.stamper.mapreduce.map.key.IdFieldEmittableKey("test.id.key.field");
        key.setId(new Text("1234"));

        org.slc.sli.stamper.mapreduce.map.key.IdFieldEmittableKey key2 = new org.slc.sli.stamper.mapreduce.map.key.IdFieldEmittableKey();
        key2.setId(new Text("1234"));

        assertEquals(key.hashCode(), key2.hashCode());

        key.setId(new Text(""));
        assertFalse(key.hashCode() == key2.hashCode());
    }

    @Test
    public void testCompareAndEqual() {
        org.slc.sli.stamper.mapreduce.map.key.IdFieldEmittableKey key = new org.slc.sli.stamper.mapreduce.map.key.IdFieldEmittableKey("test.id.key.field");
        key.setId(new Text("abc"));

        org.slc.sli.stamper.mapreduce.map.key.IdFieldEmittableKey key2 = new org.slc.sli.stamper.mapreduce.map.key.IdFieldEmittableKey("test.id.key.field2");
        key2.setId(new Text("123"));

        org.slc.sli.stamper.mapreduce.map.key.IdFieldEmittableKey key3 = new org.slc.sli.stamper.mapreduce.map.key.IdFieldEmittableKey("test.id.key.field2");
        key3.setId(new Text("123"));

        assertTrue(key.compareTo(key2) > 0);
        assertTrue(key2.compareTo(key) < 0);
        assertTrue(key2.compareTo(key3) == 0);
        assertTrue(key.compareTo(new TenantAndIdEmittableKey()) == -1);

        assertTrue(key.equals(key));
        assertTrue(key2.equals(key3));
        assertFalse(key.equals(key2));
        assertFalse(key.equals(key2));
        assertFalse(key.equals(null));
        assertFalse(key.equals(Boolean.TRUE));

        key2.setId(new Text(""));
        assertFalse(key3.equals(key2));

        key3.setId(new Text(""));
        assertTrue(key2.equals(key3));
    }

    @Test
    public void testReadWrite() throws IOException {
        org.slc.sli.stamper.mapreduce.map.key.IdFieldEmittableKey control = new org.slc.sli.stamper.mapreduce.map.key.IdFieldEmittableKey("test.id.key.field");
        control.setId(new Text("abc"));

        org.slc.sli.stamper.mapreduce.map.key.IdFieldEmittableKey key = new org.slc.sli.stamper.mapreduce.map.key.IdFieldEmittableKey();

        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(outBuf);
        control.write(outStream);

        ByteArrayInputStream inBuf = new ByteArrayInputStream(outBuf.toByteArray());
        DataInputStream inStream = new DataInputStream(inBuf);
        key.readFields(inStream);

        assertEquals(control, key);
    }

}
