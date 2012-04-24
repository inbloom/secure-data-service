package org.slc.sli.ingestion.dal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junitx.util.PrivateAccessor;

import com.mongodb.DBCollection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author tke
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class MongoIndexManagerTest {

    @Autowired
    MongoIndexManager manager;
    MongoTemplate template;
    String rootDir = "mongoIndexes";
    String batchJobId = "testBatchJob";

    @Test
    public void testCreateIndex() {
        Map<String, List<IndexDefinition>> res = MongoIndexManager.getCollectionIndexes();
        Assert.assertEquals(1, res.size());
        Assert.assertEquals(2, res.get("student").size());
        Assert.assertEquals(3, res.get("student").get(1).getIndexKeys().keySet().size());
        Assert.assertTrue(res.get("student").get(1).getIndexKeys().containsField("body.sex"));
        Assert.assertTrue(res.get("student").get(1).getIndexKeys().containsField("body.name"));
        Assert.assertTrue(res.get("student").get(1).getIndexKeys().containsField("body.birthDate"));
        Assert.assertTrue(res.get("student").get(0).getIndexKeys().containsField("metaData.tenantId"));
    }

    @Test
    public void testSetIndex() throws NoSuchFieldException {
        template = Mockito.mock(MongoTemplate.class);

        Map<String, List<IndexDefinition>> collectionIndexes = new HashMap<String, List<IndexDefinition>>();
        List<IndexDefinition> indexDefinitions = new ArrayList<IndexDefinition>();
        indexDefinitions.add(Mockito.mock(IndexDefinition.class));
        collectionIndexes.put("collection", indexDefinitions);
        PrivateAccessor.setField(manager, "collectionIndexes", collectionIndexes);

        Mockito.when(template.collectionExists(Mockito.anyString())).thenReturn(false);
        DBCollection dbcollection = Mockito.mock(DBCollection.class);
        Mockito.when(template.createCollection(Mockito.anyString())).thenReturn(dbcollection);
        manager.ensureIndex(template, batchJobId);
        Mockito.verify(template, Mockito.times(1)).ensureIndex(Mockito.any(IndexDefinition.class), Mockito.anyString());
    }

}
