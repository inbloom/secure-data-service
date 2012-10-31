/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
