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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
        
        BSONObject elem = new BasicBSONObject("id", 3697);
        BSONObject data = new BasicBSONObject("element", elem);
        final BSONObject entity = new BasicBSONObject("data", data);
        IDMapper mapper = new IDMapper(IdFieldEmittableKey.class, fields);
        
        OutputCollector<EmittableKey, BSONObject> collector = Mockito.mock(OutputCollector.class);
        
        PowerMockito.when(collector, "collect", Matchers.any(EmittableKey.class),
            Matchers.any(BSONObject.class)).thenAnswer(new Answer<BSONObject>() {
            
            @Override
            public BSONObject answer(InvocationOnMock invocation) throws Throwable {
                
                Object[] args = invocation.getArguments();
                
                assertNotNull(args);
                assertEquals(args.length, 2);
                
                assertTrue(args[0] instanceof IdFieldEmittableKey);
                assertTrue(args[1] instanceof BSONObject);
                
                IdFieldEmittableKey id = (IdFieldEmittableKey) args[0];
                assertEquals(id.getIdField().toString(), "data.element.id");
                Text idValue = id.getId();
                assertEquals(Long.parseLong(idValue.toString()), 3697);
                
                BSONObject e = (BSONObject) args[1];
                assertEquals(e, entity);
                
                return null;
            }
        });
        
        IdFieldEmittableKey id = new IdFieldEmittableKey();
        id.setFieldNames(fields);
        mapper.map(id, entity, collector, null);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testMapTenantAndIdKey() throws Exception {
        
        String[] fields = { "metaData.tenantId", "data.element.id" };
        
        BSONObject elem = new BasicBSONObject("id", 90210);
        BSONObject data = new BasicBSONObject("element", elem);
        final BSONObject entity = new BasicBSONObject("data", data);
        
        BSONObject tenantId = new BasicBSONObject("tenantId", "Midgar");
        entity.put("metaData", tenantId);
        
        IDMapper mapper = new IDMapper(TenantAndIdEmittableKey.class, fields);
        
        OutputCollector<EmittableKey, BSONObject> collector = Mockito.mock(OutputCollector.class);
        
        PowerMockito.when(collector, "collect", Matchers.any(TenantAndIdEmittableKey.class),
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
        mapper.map(id, entity, collector, null);
    }
    
}
