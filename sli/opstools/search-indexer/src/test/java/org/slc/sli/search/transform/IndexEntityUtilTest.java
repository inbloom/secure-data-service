package org.slc.sli.search.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.entity.IndexEntity.Action;
import org.slc.sli.search.util.IndexEntityUtil;

public class IndexEntityUtilTest {
    private static final String NEW_LINE = System.getProperty("line.separator");
    @Test
    public void testUtil() {
        Map<String, Object> ht = new HashMap<String, Object>();
        ht.put("x", "y");
        IndexEntity ie = new IndexEntity(Action.QUICK_UPDATE, "test", "type", "1", null, ht);
        Assert.assertEquals("{\"params\":{\"x\":\"y\"},\"script\":\"ctx._source.x=x;\"}", IndexEntityUtil.toUpdateJson(ie));
        
        List<IndexEntity> docs = new ArrayList<IndexEntity>();
        docs.add(new IndexEntity(Action.INDEX, "test", "type", "1", null, ht));
        docs.add(new IndexEntity(Action.INDEX, "test1", "type2", "2", null, ht));
        Assert.assertEquals("{\"docs\": [{\"_index\":\"test\", \"_type\":\"type\",\"_id\":\"1\"}," + NEW_LINE + 
                            "{\"_index\":\"test1\", \"_type\":\"type2\",\"_id\":\"2\"}" + NEW_LINE + "]}", 
            IndexEntityUtil.getBulkGetJson(docs));
    }
}
