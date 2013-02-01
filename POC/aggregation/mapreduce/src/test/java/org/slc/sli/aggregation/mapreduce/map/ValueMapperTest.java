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
import static org.junit.Assert.assertTrue;

import com.mongodb.hadoop.io.BSONWritable;

import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import org.slc.sli.aggregation.mapreduce.map.key.EmittableKey;
import org.slc.sli.aggregation.mapreduce.map.key.TenantAndIdEmittableKey;

/**
 * ValueMapperTest
 */
public class ValueMapperTest {

    private class MockValueMapper extends ValueMapper {

        @Override
        public Writable getValue(BSONWritable entity) {
            if (entity.containsField("found")) {
                return new ContentSummary(1, 2, 3);
            } else {
                return NullWritable.get();
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testMap() throws Exception {
        TenantAndIdEmittableKey key = new TenantAndIdEmittableKey();
        ValueMapper m = new MockValueMapper();
        BSONObject entry = new BasicBSONObject("found", "data");
        BSONWritable entity = new BSONWritable(entry);

        Context context = Mockito.mock(Context.class);
        PowerMockito.when(context, "write", Matchers.any(EmittableKey.class),
            Matchers.any(BSONObject.class)).thenAnswer(new Answer<BSONObject>() {

            @Override
            public BSONObject answer(InvocationOnMock invocation) throws Throwable {

                Object[] args = invocation.getArguments();

                assertNotNull(args);
                assertEquals(args.length, 2);

                assertTrue(args[0] instanceof TenantAndIdEmittableKey);
                assertTrue(args[1] instanceof ContentSummary);

                TenantAndIdEmittableKey id = (TenantAndIdEmittableKey) args[0];
                assertNotNull(id);

                ContentSummary e = (ContentSummary) args[1];
                assertEquals(e.getLength(), 1);
                assertEquals(e.getFileCount(), 2);
                assertEquals(e.getDirectoryCount(), 3);

                return null;
            }
        });

        m.map(key, entity, context);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testMapValueNotFound() throws Exception {
        TenantAndIdEmittableKey key = new TenantAndIdEmittableKey();
        ValueMapper m = new MockValueMapper();
        BSONObject entry = new BasicBSONObject("not_found", "data");
        BSONWritable entity = new BSONWritable(entry);

        Context context = Mockito.mock(Context.class);
        PowerMockito.when(context, "write", Matchers.any(TenantAndIdEmittableKey.class),
            Matchers.any(BSONObject.class)).thenAnswer(new Answer<BSONObject>() {

            @Override
            public BSONObject answer(InvocationOnMock invocation) throws Throwable {

                Object[] args = invocation.getArguments();

                assertNotNull(args);
                assertEquals(args.length, 2);

                assertTrue(args[0] instanceof TenantAndIdEmittableKey);
                assertTrue(args[1] instanceof NullWritable);

                return null;
            }
        });

        m.map(key, entity, context);
    }
}
