package org.slc.sli.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mongodb.DBObject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;

/** Tests the Mongo Entity. */

public class MongoEntityTest {

    private UUIDGeneratorStrategy mockGeneratorStrategy;

    private static final String FIXED_UUID = "2012wd-type1uuid"; //new UUID(42L, 5150L);

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

        assertEquals(entity2.getEntityId(), FIXED_UUID.toString());
        assertTrue(obj.get("_id") instanceof UUID);
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
        MongoEntity entity = new MongoEntity("student", uuid, body, metaData);

        return entity;
    }

}
