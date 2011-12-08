package org.slc.sli.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import com.mongodb.DBObject;

/** Tests the Mongo Entity. */

public class MongoEntityTest {

    @Test
    public void testUUID() {

        UUID uuid = UUID.randomUUID();

        Map<String, Object> body = new HashMap<String, Object>();
        Map<String, Object> metaData = new HashMap<String, Object>();
        MongoEntity entity = new MongoEntity("student", uuid.toString(), body,
                metaData);

        DBObject obj = entity.toDBObject();

        MongoEntity entity2 = MongoEntity.fromDBObject(obj);

        assertEquals(entity2.getEntityId(), uuid.toString());
        assertTrue(obj.get("_id") instanceof UUID);
    }

}
