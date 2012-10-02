package org.slc.sli.search.process;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.util.IndexEntityConverter;
import org.slc.sli.search.util.SearchIndexerException;

public class IndexEntityConverterTest {
    private IndexEntityConverter indexEntityConverter = new IndexEntityConverter();
    
    @Before
    public void setup() {
        indexEntityConverter.setDecrypt(false);
    }
    
    @Test
    public void testToIndexEntity() throws Exception {
        String entity = "{\"_id\": \"1\", \"type\": \"test\", \"body\":{\"name\":\"a\", \"b\":\"x\"}, \"metaData\": {\"tenantId\": \"tenant\"}}";
        IndexEntity indexEntity = indexEntityConverter.fromEntityJson(entity);
        Assert.assertEquals("1", indexEntity.getId());
        Assert.assertEquals("tenant", indexEntity.getIndex());
        Assert.assertEquals("test", indexEntity.getType());
        Assert.assertEquals("{\"_id\":\"1\",\"type\":\"test\",\"body\":{\"name\":\"a\",\"b\":\"x\"},\"metaData\":{\"tenantId\":\"tenant\"}}", indexEntity.getBody());
    }
    
    @Test
    public void testException() throws Exception {
        String entity = "{\"_id\": \"1\", \"type\": \"test\", \"body\":{\"name\":\"a\", \"b\":\"x\"}}";
        try {
          indexEntityConverter.fromEntityJson(entity);
          Assert.fail("Does not include metaData - should fail");
        } catch (SearchIndexerException sie) {
        }
    }
    
}
