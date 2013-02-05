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

package org.slc.sli.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.validation.NoNaturalKeysDefinedException;
import org.slc.sli.validation.schema.NaturalKeyExtractor;

/** Tests the Mongo Entity. */

public class MongoEntityTest {
    
    private UUIDGeneratorStrategy mockGeneratorStrategy;
    
    private NaturalKeyExtractor mockNaturalKeyExtractor;
    
    private static final String FIXED_UUID = "2012wd-type1uuid"; // new UUID(42L, 5150L);
    
    @Before
    public void init() {
        
        mockGeneratorStrategy = Mockito.mock(UUIDGeneratorStrategy.class);
        
        when(mockGeneratorStrategy.generateId()).thenReturn(FIXED_UUID);
        when(mockGeneratorStrategy.generateId(new NaturalKeyDescriptor(Mockito.anyMap()))).thenReturn(FIXED_UUID);
        
        mockNaturalKeyExtractor = Mockito.mock(NaturalKeyExtractor.class);
    }
    
    @Test
    public void testUUID() throws NoNaturalKeysDefinedException {
        
        Map<String, Object> body = new HashMap<String, Object>();
        
        MongoEntity entity = new MongoEntity("student", body);
        
        NaturalKeyDescriptor desc = new NaturalKeyDescriptor();
        when(mockNaturalKeyExtractor.getNaturalKeyDescriptor(entity)).thenReturn(desc);
        when(mockGeneratorStrategy.generateId(desc)).thenReturn(FIXED_UUID);
        
        DBObject obj = entity.toDBObject(mockGeneratorStrategy, mockNaturalKeyExtractor);
        
        MongoEntity entity2 = MongoEntity.fromDBObject(obj);
        
        assertEquals(entity2.getEntityId(), FIXED_UUID);
        assertTrue(obj.get("_id") instanceof String);
    }
    
    @Test
    public void testUUIDNoStrategy() {
        
        String uuid = UUID.randomUUID().toString();
        
        MongoEntity entity = createEntity(uuid);
        
        DBObject obj = entity.toDBObject(null, mockNaturalKeyExtractor);
        
        MongoEntity entity2 = MongoEntity.fromDBObject(obj);
        
        assertEquals(entity2.getEntityId(), uuid.toString());
        assertTrue(obj.get("_id") instanceof String);
    }
    
    private MongoEntity createEntity(String uuid) {
        
        Map<String, Object> body = new HashMap<String, Object>();
        Map<String, Object> metaData = new HashMap<String, Object>();
        MongoEntity entity = new MongoEntity("student", uuid, body, metaData, null, null);
        
        return entity;
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testCreateCalculatedValue() throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        Map<String, Object> body = new HashMap<String, Object>();
        Map<String, Object> calcValue = new HashMap<String, Object>();
        Map<String, Object> assessments = new HashMap<String, Object>();
        Map<String, Object> mathTest = new HashMap<String, Object>();
        Map<String, Object> highestEver = new HashMap<String, Object>();
        highestEver.put("ScaleScore", "28.0");
        mathTest.put("HighestEver", highestEver);
        assessments.put("ACT", mathTest);
        calcValue.put("assessments", assessments);
        DBObject dbObject = new BasicDBObjectBuilder().add("_id", "42").add("body", body)
                .add("calculatedValues", calcValue).get();
        CalculatedData<String> data = MongoEntity.fromDBObject(dbObject).getCalculatedValues();
        assertEquals(
                Arrays.asList(new CalculatedDatum<String>("assessments", "HighestEver", "ACT", "ScaleScore", "28.0")),
                data.getCalculatedValues());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testCreateAggregate() {
        Map<String, Object> body = new HashMap<String, Object>();
        Map<String, Object> aggregate = new HashMap<String, Object>();
        Map<String, Object> assessments = new HashMap<String, Object>();
        Map<String, Object> mathTest = new HashMap<String, Object>();
        Map<String, Integer> highestEver = new HashMap<String, Integer>();
        highestEver.put("E", 15);
        highestEver.put("2", 20);
        mathTest.put("HighestEver", highestEver);
        assessments.put("ACT", mathTest);
        aggregate.put("assessments", assessments);
        DBObject dbObject = new BasicDBObjectBuilder().add("_id", "42").add("body", body)
                .add("aggregations", aggregate).get();
        CalculatedData<Map<String, Integer>> data = MongoEntity.fromDBObject(dbObject).getAggregates();
        assertEquals(Arrays.asList(new CalculatedDatum<Map<String, Integer>>("assessments", "HighestEver", "ACT",
                "aggregate", highestEver)), data.getCalculatedValues());
        
    }
    
}
