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

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.OutputCollector;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.slc.sli.aggregation.mapreduce.map.key.EmittableKey;
import org.slc.sli.aggregation.mapreduce.map.key.IdFieldEmittableKey;
import org.slc.sli.aggregation.mapreduce.map.key.TenantAndIdEmittableKey;

/**
 * IDMapperTest
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ IDMapper.class, OutputCollector.class })
public class IDMapperTest {

    /**
     * testMapIdFieldKey Test mapping an arbitrary field to an ID.
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testMapIdFieldKey() throws Exception {
        String[] fields = { "data.element.id" };

        BSONObject elem = new BasicBSONObject("value", 7631);
        BSONObject data = new BasicBSONObject("element", elem);
        BSONObject entry = new BasicBSONObject("data", data);
        final BSONWritable entity = new BSONWritable(entry);

        IDMapper<IdFieldEmittableKey> mapper = new IDMapper<IdFieldEmittableKey>();

        IDMapper<IdFieldEmittableKey>.Context context = Mockito.mock(IDMapper.Context.class);
        PowerMockito.when(context, "write", Matchers.any(EmittableKey.class),
            Matchers.any(BSONObject.class)).thenAnswer(new Answer<BSONObject>() {

            @Override
            public BSONObject answer(InvocationOnMock invocation) throws Throwable {

                Object[] args = invocation.getArguments();

                assertNotNull(args);
                assertEquals(args.length, 2);

                assertTrue(args[0] instanceof IdFieldEmittableKey);
                assertTrue(args[1] instanceof BSONWritable);

                IdFieldEmittableKey id = (IdFieldEmittableKey) args[0];
                assertEquals(id.getIdField().toString(), "data.element.id");
                Text idValue = id.getId();
                assertEquals(Long.parseLong(idValue.toString()), 3697);

                BSONWritable e = (BSONWritable) args[1];
                assertEquals(e, entity);

                return null;
            }
        });

        IdFieldEmittableKey id = new IdFieldEmittableKey();
        id.setFieldNames(fields);
        id.setId(new Text("3697"));
        mapper.map(id, entity, context);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMapTenantAndIdKey() throws Exception {

        String[] fields = { "metaData.tenantId", "data.element.id" };

        BSONObject elem = new BasicBSONObject("value", 7632);
        BSONObject data = new BasicBSONObject("element", elem);
        BSONObject entry = new BasicBSONObject("data", data);
        final BSONWritable entity = new BSONWritable(entry);

        BSONObject tenantId = new BasicBSONObject("EdOrgs", "Midtown");
        entity.put("metaData", tenantId);

        IDMapper<TenantAndIdEmittableKey> mapper = new IDMapper<TenantAndIdEmittableKey>();
        IDMapper<TenantAndIdEmittableKey>.Context context = Mockito.mock(IDMapper.Context.class);
        PowerMockito.when(context, "write", Matchers.any(EmittableKey.class),
            Matchers.any(BSONObject.class)).thenAnswer(new Answer<BSONObject>() {

            @Override
            public BSONObject answer(InvocationOnMock invocation) throws Throwable {

                Object[] args = invocation.getArguments();

                assertNotNull(args);
                assertEquals(args.length, 2);

                assertTrue(args[0] instanceof TenantAndIdEmittableKey);
                assertTrue(args[1] instanceof BSONObject);

                TenantAndIdEmittableKey id = (TenantAndIdEmittableKey) args[0];
                assertEquals(id.getIdField().toString(), "data.element.id");
                Text idValue = id.getId();
                assertEquals(Long.parseLong(idValue.toString()), 90210);

                assertEquals(id.getTenantIdField().toString(), "metaData.tenantId");
                idValue = id.getTenantId();
                assertEquals(idValue.toString(), "Midgar");

                BSONObject e = (BSONObject) args[1];
                assertEquals(e, entity);

                return null;
            }
        });

        TenantAndIdEmittableKey id = new TenantAndIdEmittableKey();
        id.setFieldNames(fields);
        id.setTenantId(new Text("Midgar"));
        id.setId(new Text("90210"));
        mapper.map(id, entity, context);
    }
}
