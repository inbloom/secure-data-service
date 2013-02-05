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

package org.slc.sli.dal.convert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mongodb.DBObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.validation.NoNaturalKeysDefinedException;
import org.slc.sli.validation.schema.INaturalKeyExtractor;
import org.springframework.context.ApplicationContext;

/**
 * EntityWriteConverter unit-tests.
 * 
 * @author okrook
 * 
 */
public class EntityWriteConverterTest {
    
    @InjectMocks
    EntityWriteConverter converter = new EntityWriteConverter();
    
    @Mock
    EntityEncryption encryptor;
    List<Object[]> encryptCalls = new ArrayList<Object[]>();
    
    @Mock
    UUIDGeneratorStrategy uuidGeneratorStrategy;
    
    @Mock
    INaturalKeyExtractor naturalKeyExtractor;
    
    @Mock
    ApplicationContext mockContext;
    
    @SuppressWarnings("unchecked")
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(encryptor.encrypt(Mockito.anyString(), Mockito.anyMap())).thenAnswer(
                new Answer<Map<String, Object>>() {
                    @Override
                    public Map<String, Object> answer(InvocationOnMock invocation) throws Throwable {
                        encryptCalls.add(invocation.getArguments());
                        return (Map<String, Object>) invocation.getArguments()[1];
                    }
                });
        Mockito.when(mockContext.getBean("entityEncryption", EntityEncryption.class)).thenReturn(encryptor);
    }
    
    @Test
    public void testEntityConvert() throws NoNaturalKeysDefinedException {
        Entity e = Mockito.mock(Entity.class);
        
        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("field1", "field1");
        body.put("field2", "field2");
        
        HashMap<String, Object> meta = new HashMap<String, Object>();
        meta.put("meta1", "field1");
        meta.put("meta2", "field2");
        
        NaturalKeyDescriptor desc = new NaturalKeyDescriptor();
        
        Mockito.when(e.getType()).thenReturn("collection");
        Mockito.when(e.getBody()).thenReturn(body);
        Mockito.when(e.getMetaData()).thenReturn(meta);
        
        Mockito.when(naturalKeyExtractor.getNaturalKeyDescriptor(Mockito.any(Entity.class))).thenReturn(desc);
        Mockito.when(uuidGeneratorStrategy.generateId(desc)).thenReturn("uid");
        
        DBObject d = converter.convert(e);
        Assert.assertNotNull("DBObject should not be null", d);
        
        assertSame(body, (Map<?, ?>) d.get("body"));
        assertSame(meta, (Map<?, ?>) d.get("metaData"));
        
        Assert.assertEquals(1, encryptCalls.size());
        Assert.assertEquals(2, encryptCalls.get(0).length);
        Assert.assertEquals(e.getType(), encryptCalls.get(0)[0]);
        Assert.assertEquals(e.getBody(), encryptCalls.get(0)[1]);
    }
    
    @Test
    public void testMockMongoEntityConvert() {
        MongoEntity e = Mockito.mock(MongoEntity.class);
        
        DBObject result = Mockito.mock(DBObject.class);
        Mockito.when(e.toDBObject(uuidGeneratorStrategy, naturalKeyExtractor)).thenReturn(result);
        
        DBObject d = converter.convert(e);
        Assert.assertNotNull(d);
        
        Assert.assertEquals(result, d);
        Mockito.verify(e, Mockito.times(1)).encrypt(encryptor);
        
        Mockito.verify(e, Mockito.times(1)).toDBObject(uuidGeneratorStrategy, naturalKeyExtractor);
    }
    
    @Test
    public void testMongoEntityConvert() {
        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("field1", "field1");
        body.put("field2", "field2");
        
        HashMap<String, Object> meta = new HashMap<String, Object>();
        meta.put("meta1", "field1");
        meta.put("meta2", "field2");
        
        MongoEntity e = new MongoEntity("collection", UUID.randomUUID().toString(), body, meta);
        
        DBObject d = converter.convert(e);
        Assert.assertNotNull(d);
        
        assertSame(body, (Map<?, ?>) d.get("body"));
        assertSame(meta, (Map<?, ?>) d.get("metaData"));
        
        Assert.assertEquals(1, encryptCalls.size());
        Assert.assertEquals(2, encryptCalls.get(0).length);
        Assert.assertEquals(e.getType(), encryptCalls.get(0)[0]);
        Assert.assertEquals(e.getBody(), encryptCalls.get(0)[1]);
    }
    
    private static void assertSame(Map<?, ?> m1, Map<?, ?> m2) {
        if (m1 == null && m2 == null) {
            return;
        }
        
        Assert.assertNotNull(m1);
        Assert.assertNotNull(m2);
        
        if (m1 == m2) {
            return;
        }
        
        Assert.assertArrayEquals(m1.keySet().toArray(), m2.keySet().toArray());
        
        for (Map.Entry<?, ?> e : m1.entrySet()) {
            Assert.assertEquals(e.getValue(), m2.get(e.getKey()));
        }
    }
    
}
