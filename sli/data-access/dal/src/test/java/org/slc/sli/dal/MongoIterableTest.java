package org.slc.sli.dal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 * Junit for MongoIterable
 *
 * @author nbrown
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class MongoIterableTest {

    @Resource(name = "mongoEntityRepository")
    private Repository<Entity> repository;

    private final String collectionName = "iterableTest";
    private DBCollection collection;
    private DBObject[] objects = new DBObject[] { new BasicDBObject("test", "1"), new BasicDBObject("test", "2"),
            new BasicDBObject("test", "3") };

    @Before
    public void setup() {
        collection = repository.getCollection(collectionName);
        collection.drop();
        for (DBObject o : objects) {
            collection.insert(objects);

        }
    }

    @Test
    public void testGetAll() {
        Iterable<DBObject> it = new MongoIterable(collection, new BasicDBObject(), 2);
        Set<DBObject> results = new HashSet<DBObject>();
        for (DBObject db : it) {
            results.add(db);
        }
        assertEquals(3, results.size());
        assertTrue(results.containsAll(Arrays.asList(objects)));
    }

    @Test
    public void testGetAllSinglePage() {
        Iterable<DBObject> it = new MongoIterable(collection, new BasicDBObject(), 3);
        Set<DBObject> results = new HashSet<DBObject>();
        for (DBObject db : it) {
            results.add(db);
        }
        assertEquals(3, results.size());
        assertTrue(results.containsAll(Arrays.asList(objects)));
    }

    @Test
    public void testGetAllWithQuery() {
        Iterable<DBObject> it = new MongoIterable(collection, new BasicDBObject("test", "1"), 2);
        Set<DBObject> results = new HashSet<DBObject>();
        for (DBObject db : it) {
            results.add(db);
        }
        assertEquals(1, results.size());
        assertTrue(results.contains(objects[0]));
    }

}
