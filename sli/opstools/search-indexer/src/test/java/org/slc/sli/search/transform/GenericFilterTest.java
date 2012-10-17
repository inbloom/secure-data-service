package org.slc.sli.search.transform;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.config.IndexConfigStore;
import org.slc.sli.search.transform.impl.GenericFilter;
import org.slc.sli.search.util.NestedMapUtil;

public class GenericFilterTest {
    IndexConfigStore store;
    
    @Before
    public void setup() throws Exception {
        store = new IndexConfigStore("index-config-test.json");
    }
    @Test
    public void testFilter() {
        Map<String, Object> testMap = new HashMap<String, Object>();
        NestedMapUtil.put(NestedMapUtil.getPathLinkFromDotNotation("test.levl1.levl2"), 1, testMap);
        NestedMapUtil.put(NestedMapUtil.getPathLinkFromDotNotation("test.levl1.name"), "my name", testMap);
        NestedMapUtil.put(NestedMapUtil.getPathLinkFromDotNotation("test.filter"), "1", testMap);
        
        IndexConfig config = store.getConfig("test");
        GenericFilter filter = new GenericFilter();
        Assert.assertFalse("entity does not match condition", filter.matchesCondition(config, testMap));
        
        NestedMapUtil.put(NestedMapUtil.getPathLinkFromDotNotation("test.filter"), null, testMap);
        Assert.assertTrue("entity must match condition since value matches", filter.matchesCondition(config, testMap));
        
        NestedMapUtil.remove(NestedMapUtil.getPathLinkFromDotNotation("test.filter"), testMap);
        Assert.assertTrue("entity must match condition since value is missing and equavalent to null", filter.matchesCondition(config, testMap));
    }
    
}
