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

package org.slc.sli.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;

/** Tests the Mongo Entity. */

public class MongoEntityTest {

    private UUIDGeneratorStrategy mockGeneratorStrategy;

    private static final String FIXED_UUID = "2012wd-type1uuid"; // new UUID(42L, 5150L);

    @Before
    public void init() {

        mockGeneratorStrategy = Mockito.mock(UUIDGeneratorStrategy.class);

        when(mockGeneratorStrategy.randomUUID()).thenReturn(FIXED_UUID);
    }

    @Test
    public void testUUID() {

        Map<String, Object> body = new HashMap<String, Object>();

        MongoEntity entity = new MongoEntity("student", body);

        DBObject obj = entity.toDBObject(mockGeneratorStrategy);

        MongoEntity entity2 = MongoEntity.fromDBObject(obj);

        assertEquals(entity2.getEntityId(), FIXED_UUID);
        assertTrue(obj.get("_id") instanceof String);
    }

    @Test
    public void testUUIDNoStrategy() {

        String uuid = UUID.randomUUID().toString();

        MongoEntity entity = createEntity(uuid);

        DBObject obj = entity.toDBObject(null);

        MongoEntity entity2 = MongoEntity.fromDBObject(obj);

        assertEquals(entity2.getEntityId(), uuid.toString());
        assertTrue(obj.get("_id") instanceof String);
    }

    private MongoEntity createEntity(String uuid) {

        Map<String, Object> body = new HashMap<String, Object>();
        Map<String, Object> metaData = new HashMap<String, Object>();
        MongoEntity entity = new MongoEntity("student", uuid, body, metaData, null);

        return entity;
    }

    @Test
    public void testCreateAggregate() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Map<String, Object> body = new HashMap<String, Object>();
        Map<String, Object> aggregate = new HashMap<String, Object>();
        Map<String, Object> assessments = new HashMap<String, Object>();
        Map<String, Object> mathTest = new HashMap<String, Object>();
        Map<String, Object> highestEver = new HashMap<String, Object>();
        highestEver.put("ScaleScore", "28.0");
        mathTest.put("HighestEver", highestEver);
        assessments.put("Grade 7 2011 State Math", mathTest);
        aggregate.put("assessments", assessments);
        DBObject dbObject = new BasicDBObjectBuilder().add("_id", "42").add("body", body)
                .add("aggregations", aggregate).get();
        AggregateData data = MongoEntity.fromDBObject(dbObject).getAggregates();
        assertEquals(new HashSet<String>(Arrays.asList("assessments")), data.getAggregatedTypes());
        Map<String, Object> assessmentAggs = data.getAggregatesForType("assessments");
        assertEquals("28.0", BeanUtils.getProperty(assessmentAggs, "Grade 7 2011 State Math.HighestEver.ScaleScore"));
    }

}
