package org.slc.sli.dal.convert;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mongodb.DBObject;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;

/**
 * EntityWriteConverter unit-tests.
 *
 * @author okrook
 *
 */
public class EntityWriteConverterTest {

    @Test
    public void testEntityConvert() {
        Entity e = Mockito.mock(Entity.class);

        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("field1", "field1");
        body.put("field2", "field2");

        HashMap<String, Object> meta = new HashMap<String, Object>();
        meta.put("meta1", "field1");
        meta.put("meta2", "field2");

        Mockito.when(e.getType()).thenReturn("collection");
        Mockito.when(e.getBody()).thenReturn(body);
        Mockito.when(e.getMetaData()).thenReturn(meta);

        EntityWriteConverter converter = new EntityWriteConverter();

        DBObject d = converter.convert(e);
        Assert.assertNotNull(d);

        assertSame(body, (Map<?, ?>) d.get("body"));
        assertSame(meta, (Map<?, ?>) d.get("metaData"));
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

        EntityWriteConverter converter = new EntityWriteConverter();

        DBObject d = converter.convert(e);
        Assert.assertNotNull(d);

        assertSame(body, (Map<?, ?>) d.get("body"));
        assertSame(meta, (Map<?, ?>) d.get("metaData"));
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
