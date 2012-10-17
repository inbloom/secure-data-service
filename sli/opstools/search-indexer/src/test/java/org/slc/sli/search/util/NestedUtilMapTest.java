package org.slc.sli.search.util;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.slc.sli.search.util.NestedMapUtil;

public class NestedUtilMapTest {
    
    @SuppressWarnings("unchecked")
    @Test
    public void testFlatMap() {
        Map<String, Object> testMap = new HashMap<String, Object>();
        NestedMapUtil.put(NestedMapUtil.getPathLinkFromDotNotation("test.levl1.levl2"), 1, testMap);
        NestedMapUtil.put(NestedMapUtil.getPathLinkFromDotNotation("test.levl1.name"), "my name", testMap);
        Map<String, Object> map = (Map<String, Object>) ((Map<String, Object>)testMap.get("test")).get("levl1");
        Assert.assertEquals(1, map.get("levl2"));
        Assert.assertEquals("my name", map.get("name"));
        
        Map<String, Object> flatMap = NestedMapUtil.toFlatMap(testMap);
        Assert.assertEquals(2, flatMap.size());
        Assert.assertEquals("my name", flatMap.get("test.levl1.name"));
        Assert.assertEquals(1, flatMap.get("test.levl1.levl2"));
    }
    
}
