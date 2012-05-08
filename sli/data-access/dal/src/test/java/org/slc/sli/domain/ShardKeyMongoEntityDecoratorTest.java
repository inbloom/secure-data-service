package org.slc.sli.domain;

import com.mongodb.BasicDBObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * User: wscott
 */
public class ShardKeyMongoEntityDecoratorTest {

    //Class Under Test
    private ShardKeyMongoEntityDecorator shardKeyMongoEntityDecorator;

    Map<String, List<String>> shardKeys;
    BasicDBObject dbObject;

    @Before
    public void initTest() {
        //set up test shard key definition
        shardKeyMongoEntityDecorator = new ShardKeyMongoEntityDecorator();
        shardKeys = new HashMap<String, List<String>>();
        List<String> testShardKeys = new ArrayList<String>();
        testShardKeys.add("metaData.key1");
        testShardKeys.add("body.key2");
        testShardKeys.add("_id");
        shardKeys.put("test", testShardKeys);
        shardKeyMongoEntityDecorator.setShardDefinitions(shardKeys);

        //set up test database objects
        dbObject = new BasicDBObject();
        Map<String, Object> body = new BasicDBObject();
        Map<String, Object> metaData = new BasicDBObject();

        metaData.put("key1", "key1Value");
        body.put("key2", "key2Value");

        dbObject.put("metaData", metaData);
        dbObject.put("body", body);
        dbObject.put("_id", "_idValue");
        dbObject.put("type", "test");
    }

    @Test
    public void TestShardKeySet() {
        assertNotNull(shardKeyMongoEntityDecorator);
        assertNotNull(shardKeyMongoEntityDecorator.getShardDefinitions());
    }

    @Test
    public void TestShardKeyDecorate() {
        shardKeyMongoEntityDecorator.decorate(dbObject);
        assertNotNull(dbObject.get("shardkey"));

        assertEquals("key1Valuekey2Value_idValue", dbObject.get("shardkey"));
    }

    @Test
    public void TestShardKeyDecorateNoDefinition() {
        dbObject.put("type", "unknown");

        shardKeyMongoEntityDecorator.decorate(dbObject);
        assertNull(dbObject.get("shardkey"));
    }

    @Test
    public void TestShardKeyDecorateNonExistantField() {
        List testShardKeys = shardKeys.get("test");
        testShardKeys.add("i.do.not.exist");

        shardKeyMongoEntityDecorator.decorate(dbObject);
        assertEquals("key1Valuekey2Value_idValue", dbObject.get("shardkey"));
    }
}
