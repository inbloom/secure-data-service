package org.slc.sli.ingestion.transformation.normalization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import com.mongodb.BasicDBList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.Repository;

/**
 * Test cases for ContextTaker.
 * @author vmcglaughlin
 *
 */
public class ContextTakerTest {

    @InjectMocks
    private ContextTaker contextTaker;

    @Mock
    private Repository<Entity> mockRepository;

    @Before
    public void setup() {
        contextTaker = new ContextTaker();
        MockitoAnnotations.initMocks(this);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void shouldNotChangeEntityOnNullResults() {
        Mockito.when(mockRepository.findByQuery(Mockito.anyString(), Mockito.any(Query.class), Mockito.anyInt(), Mockito.anyInt()))
            .thenReturn(null);

        Entity entity = Mockito.mock(Entity.class);
        List<String> ids = new ArrayList<String>();

        contextTaker.addContext(entity, new ArrayList<String>(), "collection", new Query(), ids);

        Mockito.verifyZeroInteractions(entity);
        Assert.assertEquals("Id list should have size zero", 0, ids.size());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void shouldNotChangeEntityOnEmptyResults() {
        Mockito.when(mockRepository.findByQuery(Mockito.anyString(), Mockito.any(Query.class), Mockito.anyInt(), Mockito.anyInt()))
            .thenReturn(new ArrayList<Entity>());

        Entity entity = Mockito.mock(Entity.class);
        List<String> ids = new ArrayList<String>();

        contextTaker.addContext(entity, new ArrayList<String>(), "collection", new Query(), ids);

        Mockito.verifyZeroInteractions(entity);
        Assert.assertEquals("Id list should have size zero", 0, ids.size());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void entityShouldTakeMultipleContextsFromResults() {
        List<Entity> entityList = getExistingEntities();
        Mockito.when(mockRepository.findByQuery(Mockito.anyString(), Mockito.any(Query.class), Mockito.anyInt(), Mockito.anyInt()))
            .thenReturn(entityList);

        Map<String, Object> metaData = new HashMap<String, Object>();
        Entity entity = new MongoEntity("type", "entity", new HashMap<String, Object>(), metaData);

        List<String> ids = new ArrayList<String>();
        List<String> takesContext = new ArrayList<String>();
        takesContext.add("context1");
        takesContext.add("context2");

        contextTaker.addContext(entity, takesContext, "collection", new Query(), ids);

        Assert.assertTrue("Id list should contain e0", ids.contains("e0"));
        Assert.assertTrue("Id list should contain e1", ids.contains("e1"));
        Assert.assertTrue("Id list should contain e2", ids.contains("e2"));
        Assert.assertTrue("Id list should contain e3", ids.contains("e3"));

        Assert.assertEquals("metaData should have two contexts", 2, metaData.size());

        Assert.assertTrue("metaData should contain context1", metaData.containsKey("context1"));
        BasicDBList context1 = (BasicDBList) metaData.get("context1");
        Assert.assertEquals("context1 should have two values", 2, context1.size());
        Assert.assertTrue("context1 should contain val1-0", context1.contains("val1-0"));
        Assert.assertTrue("context1 should contain val1-1", context1.contains("val1-1"));

        Assert.assertTrue("metaData should contain context2", metaData.containsKey("context2"));
        BasicDBList context2 = (BasicDBList) metaData.get("context2");
        Assert.assertEquals("context2 should have two values", 2, context2.size());
        Assert.assertTrue("context2 should contain val2-0", context2.contains("val2-0"));
        Assert.assertTrue("context2 should contain val2-1", context2.contains("val2-1"));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void entityShouldTakeSingleContextFromResults() {
        List<Entity> entityList = getExistingEntities();
        // additional entity for this test with five contexts, three values for each
        Entity e4 = generateEntity("e4", 5, 3);
        entityList.add(e4);

        Mockito.when(mockRepository.findByQuery(Mockito.anyString(), Mockito.any(Query.class), Mockito.anyInt(), Mockito.anyInt()))
            .thenReturn(entityList);

        Map<String, Object> metaData = new HashMap<String, Object>();
        Entity entity = new MongoEntity("type", "entity", new HashMap<String, Object>(), metaData);

        List<String> ids = new ArrayList<String>();
        List<String> takesContext = new ArrayList<String>();
        takesContext.add("context4");

        contextTaker.addContext(entity, takesContext, "collection", new Query(), ids);

        Assert.assertTrue("Id list should contain e0", ids.contains("e0"));
        Assert.assertTrue("Id list should contain e1", ids.contains("e1"));
        Assert.assertTrue("Id list should contain e2", ids.contains("e2"));
        Assert.assertTrue("Id list should contain e3", ids.contains("e3"));
        Assert.assertTrue("Id list should contain e4", ids.contains("e4"));

        Assert.assertEquals("metaData should have one context", 1, metaData.size());

        Assert.assertTrue("metaData should contain context4", metaData.containsKey("context4"));
        BasicDBList context3 = (BasicDBList) metaData.get("context4");
        Assert.assertEquals("context3 should have three values", 3, context3.size());
        Assert.assertTrue("context3 should contain val4-0", context3.contains("val4-0"));
        Assert.assertTrue("context3 should contain val4-1", context3.contains("val4-1"));
        Assert.assertTrue("context3 should contain val4-2", context3.contains("val4-2"));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void entityShouldNotTakeContextsFromResults() {
        List<Entity> entityList = getExistingEntities();
        Mockito.when(mockRepository.findByQuery(Mockito.anyString(), Mockito.any(Query.class), Mockito.anyInt(), Mockito.anyInt()))
            .thenReturn(entityList);

        Map<String, Object> metaData = new HashMap<String, Object>();
        Entity entity = new MongoEntity("type", "entity", new HashMap<String, Object>(), metaData);

        List<String> ids = new ArrayList<String>();
        List<String> takesContext = new ArrayList<String>();
        takesContext.add("context8");
        takesContext.add("context9");

        contextTaker.addContext(entity, takesContext, "collection", new Query(), ids);

        Assert.assertTrue("Id list should contain e0", ids.contains("e0"));
        Assert.assertTrue("Id list should contain e1", ids.contains("e1"));
        Assert.assertTrue("Id list should contain e2", ids.contains("e2"));
        Assert.assertTrue("Id list should contain e3", ids.contains("e3"));

        Assert.assertEquals("metaData should have zero contexts", 0, metaData.size());
    }

    private List<Entity> getExistingEntities() {
        List<Entity> entityList = new ArrayList<Entity>();

        // one context, three values
        Entity e0 = generateEntity("e0", 1, 2);
        entityList.add(e0);

        // two contexts, two values in each
        Entity e1 = generateEntity("e1", 2, 2);
        entityList.add(e1);

        // three contexts, two values in each
        Entity e2 = generateEntity("e2", 3, 2);
        entityList.add(e2);

        // no contexts
        Entity e3 = generateEntity("e3", 0, 0);
        entityList.add(e3);

        return entityList;
    }

    private Entity generateEntity(String id, int numContexts, int valuesPerContext) {
        Map<String, Object> emptyBody = new HashMap<String, Object>();
        Map<String, Object> metaData = new HashMap<String, Object>();

        for (int i = 0; i < numContexts; i++) {
            BasicDBList contextList = new BasicDBList();
            for (int j = 0; j < valuesPerContext; j++) {
                contextList.add("val" + i + "-" + j);
            }
            metaData.put("context" + i, contextList);
        }

        return new MongoEntity("type", id, emptyBody, metaData);

    }

}
