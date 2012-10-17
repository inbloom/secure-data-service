package org.slc.sli.search.transform;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slc.sli.search.config.IndexConfigStore;
import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.entity.IndexEntity.Action;
import org.slc.sli.search.transform.IndexEntityConverter;
import org.slc.sli.search.util.IndexEntityUtil;
import org.slc.sli.search.util.SearchIndexerException;

public class IndexEntityConverterTest {
    private final IndexEntityConverter indexEntityConverter = new IndexEntityConverter();
    
    @Before
    public void setup() throws Exception{
        indexEntityConverter.setDecrypt(false);
        indexEntityConverter.setIndexConfigStore(new IndexConfigStore("index-config-test.json"));
    }
    
    @Test
    public void testToIndexEntity() throws Exception {
        String entity = "{\"_id\": \"1\", \"type\": \"test\", \"body\":{\"name\":\"a\", \"a\":\"1\", \"b\":\"x\"}, \"metaData\": {\"tenantId\": \"tenant\"}}";
        IndexEntity indexEntity = indexEntityConverter.fromEntityJson(entity);
        Assert.assertEquals("a", indexEntity.getId());
        Assert.assertEquals("tenant", indexEntity.getIndex());
        Assert.assertEquals("student", indexEntity.getType());
        Assert.assertEquals("{\"a\":\"1\",\"metaData\":{\"contextId\":\"x\"}}", IndexEntityUtil.getBody(Action.INDEX, indexEntity.getBody()));
    }
    
    @Test
    public void testException() throws Exception {
        String entity = "{\"_id\": \"1\", \"type\": \"test\", \"body\":{\"b\":\"x\"}}";
        try {
          indexEntityConverter.fromEntityJson(entity);
          Assert.fail("Does not include metaData - should fail");
        } catch (SearchIndexerException sie) {
        }
    }
    
}
