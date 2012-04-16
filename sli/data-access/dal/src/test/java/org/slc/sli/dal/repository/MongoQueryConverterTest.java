package org.slc.sli.dal.repository;

import com.mongodb.DBObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.NeutralCriteria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * JUnits for DAL
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class MongoQueryConverterTest {
    private MongoQueryConverter mongoQueryConverter;

    @Before
    public void setup() {
        mongoQueryConverter = new MongoQueryConverter();
    }
    
    @Test
    public void testKeyPrefixing() {
        
        NeutralCriteria neutralCriteria1 = new NeutralCriteria("metadata.x", "=", "1");
        NeutralCriteria neutralCriteria2 = new NeutralCriteria("metadata.x", "=", "1", false);
        NeutralCriteria neutralCriteria3 = new NeutralCriteria("_id", "=", "1");
        NeutralCriteria neutralCriteria4 = new NeutralCriteria("metadata._id", "=", "1");

        assertEquals(MongoQueryConverter.prefixKey(neutralCriteria1), "body.metadata.x");
        assertEquals(MongoQueryConverter.prefixKey(neutralCriteria2), "metadata.x");
        assertEquals(MongoQueryConverter.prefixKey(neutralCriteria3), "_id");
        assertEquals(MongoQueryConverter.prefixKey(neutralCriteria4), "body.metadata._id");
    }

    @Test
    public void testAddCriteria() {
        Query query = new Query();
        NeutralCriteria neutralCriteria1 = new NeutralCriteria("eventDate", ">=", "2011-09-08");
        NeutralCriteria neutralCriteria2 = new NeutralCriteria("eventDate", "<=", "2012-04-08");

        List<NeutralCriteria> list = new ArrayList<NeutralCriteria>();
        list.add(neutralCriteria1);
        list.add(neutralCriteria2);

        Map<String, List<NeutralCriteria>> map = new HashMap<String, List<NeutralCriteria>>();
        map.put("eventDate", list);

        query = mongoQueryConverter.addCriteria(query, map);

        assertNotNull("Should not be null", query);
        DBObject obj = query.getQueryObject();
        assertTrue("Should not be null", obj.containsField("body.eventDate"));

        DBObject criteria = (DBObject) obj.get("body.eventDate");
        assertNotNull("Should not be null", criteria.get("$gte"));
        assertNotNull("Should not be null", criteria.get("$lte"));
    }

    @Test
    public void testNullAddCriteria() {
        Query query = new Query();

        query = mongoQueryConverter.addCriteria(query, null);
        assertNotNull("Should not be null", query);
        DBObject obj = query.getQueryObject();
        assertEquals("Should match", 0, obj.keySet().size());

        assertNull("Should be null", mongoQueryConverter.addCriteria(null, null));
        assertNull("Should be null", mongoQueryConverter.addCriteria(null,
                new HashMap<String, List<NeutralCriteria>>()));

    }


}
