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

package org.slc.sli.aggregation.mapreduce.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.util.MongoConfigUtil;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.security.UserGroupInformation;
import org.bson.BSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.slc.sli.aggregation.mapreduce.map.key.TenantAndIdEmittableKey;

/**
 * MongoAggWriterTest
 *
 * @param <T>
 *            Expected value for a test.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ UserGroupInformation.class, DBCollection.class, MongoAggWriter.class,
    MongoConfigUtil.class })
public class MongoAggWriterTest<T> {

    DBCollection mockCollection = null;
    UserGroupInformation ugi = null;
    TaskAttemptContext ctx = null;
    Configuration config = null;
    TenantAndIdEmittableKey key = null;
    MongoAggWriter writer = null;
    T expected = null;

    @Before
    public void init() throws IOException {
        this.expect(null);
    }

    public void expect(T expected) throws IOException {
        this.expected = expected;

        mockCollection = Mockito.mock(DBCollection.class);

        ugi = Mockito.mock(UserGroupInformation.class);

        PowerMockito.mockStatic(UserGroupInformation.class);
        Mockito.when(UserGroupInformation.getCurrentUser()).thenReturn(ugi);

        ctx = new MockTaskAttemptContext();
        config = ctx.getConfiguration();

        PowerMockito.mockStatic(MongoConfigUtil.class);
        Mockito.when(MongoConfigUtil.getOutputCollection(config)).thenReturn(mockCollection);

        key = new TenantAndIdEmittableKey("testTenant", "testId");
        key.setTenantId(new Text("Midgar"));
        key.setId(new Text("abcdefg01234567890"));
        writer = new MongoAggWriter(mockCollection, ctx);
    }

    @Test
    public void testKey() {
        Mockito
            .when(
                mockCollection.findAndModify(Matchers.any(DBObject.class),
                    Matchers.any(DBObject.class))).thenAnswer(new Answer<DBObject>() {
                @Override
                public DBObject answer(InvocationOnMock inv) {
                    Object[] args = inv.getArguments();

                    // Expect 2 objects -- key and value
                    assertTrue(args.length == 2);

                    // Both should be BSONObject types
                    assertTrue(args[0] instanceof BSONObject);
                    assertTrue(args[1] instanceof BSONObject);

                    BSONObject arg0 = (BSONObject) args[0];

                    // Key should contain two entries: tenant and id
                    assertTrue(arg0.containsField("testTenant"));
                    assertTrue(arg0.get("testTenant").toString().equals("Midgar"));
                    assertTrue(arg0.containsField("testId"));
                    assertTrue(arg0.get("testId").toString().equals("abcdefg01234567890"));

                    return null;
                }
            });
    }

    @Test
    public void testString() throws IOException {
        MongoAggWriterTest<String> obj = new MongoAggWriterTest<String>();
        obj.expect("testValue");
        obj.testWrite("testValue");
    }

    @Test
    public void testLong() throws IOException {
        MongoAggWriterTest<Long> obj = new MongoAggWriterTest<Long>();
        obj.expect(10L);
        obj.testWrite(10L);
    }

    @Test
    public void testDouble() throws IOException {
        MongoAggWriterTest<Double> obj = new MongoAggWriterTest<Double>();
        obj.expect(25.0D);
        obj.testWrite(25.0D);
    }

    @Test
    public void testList() throws IOException {
        MongoAggWriterTest<List<String>> obj = new MongoAggWriterTest<List<String>>();
        List<String> val = new LinkedList<String>();
        val.add("test1");
        val.add("test2");
        val.add("test3");
        obj.expect(val);

        val = new LinkedList<String>();
        val.add("test1");
        val.add("test2");
        val.add("test3");

        obj.testWrite(val);
    }

    @Test
    public void testMap() throws IOException {
        MongoAggWriterTest<Map<String, Long>> obj = new MongoAggWriterTest<Map<String, Long>>();
        Map<String, Long> val = new TreeMap<String, Long>();
        val.put("test1", 1L);
        val.put("test2", 2L);
        val.put("test3", 3L);
        obj.expect(val);

        val = new TreeMap<String, Long>();
        val.put("test1", 1L);
        val.put("test2", 2L);
        val.put("test3", 3L);

        obj.testWrite(val);
    }

    protected void testWrite(final T data) throws IOException {

        Mockito
            .when(
                mockCollection.findAndModify(Matchers.any(DBObject.class),
                    Matchers.any(DBObject.class))).thenAnswer(new Answer<DBObject>() {
                @Override
                public DBObject answer(InvocationOnMock inv) {
                    Object[] args = inv.getArguments();

                    // Expect 2 objects -- key and value
                    assertTrue(args.length == 2);

                    // Both should be BSONObject types
                    assertTrue(args[0] instanceof BSONObject);
                    assertTrue(args[1] instanceof BSONObject);

                    BSONObject arg1 = (BSONObject) args[1];

                    // value is a single value
                    BSONObject s = (BSONObject) arg1.get("$set");
                    assertNotNull(s);
                    assertEquals(s.get("testKey"), data);

                    return null;
                }
            });

        BSONWritable value = new BSONWritable();
        value.put("testKey", data);
        writer.write(key, value);
    }

}
