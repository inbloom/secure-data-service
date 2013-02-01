/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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
package org.slc.sli.search.transform;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.config.IndexConfigStore;
import org.slc.sli.search.transform.impl.GenericTransformer;
import org.slc.sli.search.util.DotPath;
import org.slc.sli.search.util.NestedMapUtil;

public class GenericTransformerTest {
    IndexConfigStore store;
    
    @Before
    public void setup() throws Exception {
        store = new IndexConfigStore("index-config-test.json");
    }
    @Test
    public void testFilter() {
        Map<String, Object> testMap = new HashMap<String, Object>();
        NestedMapUtil.put(new DotPath("test.levl1.levl2"), 1, testMap);
        NestedMapUtil.put(new DotPath("test.levl1.name"), "my name", testMap);
        NestedMapUtil.put(new DotPath("test.filter"), "1", testMap);
        
        IndexConfig config = store.getConfig("test");
        GenericTransformer filter = new GenericTransformer();
        Assert.assertFalse("entity does not match condition", filter.isMatch(config, testMap));
        
        NestedMapUtil.put(new DotPath("test.filter"), null, testMap);
        Assert.assertTrue("entity must match condition since value matches", filter.isMatch(config, testMap));
        
        NestedMapUtil.remove(new DotPath("test.filter"), testMap);
        Assert.assertTrue("entity must match condition since value is missing and equavalent to null", filter.isMatch(config, testMap));
    }
    
    @Test
    public void testAppend() {
        Map<String, Object> testMap = new HashMap<String, Object>();
        
        Map<String, Object> testMap1 = new HashMap<String, Object>();
        NestedMapUtil.put(new DotPath("id"), "1", testMap1);
        NestedMapUtil.put(new DotPath("filter"), "4", testMap1);
        Map<String, Object> testMap2 = new HashMap<String, Object>();
        NestedMapUtil.put(new DotPath("id"), 2, testMap2);
        Map<String, Object> testMap3 = new HashMap<String, Object>();
        NestedMapUtil.put(new DotPath("id"), "3", testMap3);
        NestedMapUtil.put(new DotPath("filter"), "3", testMap3);
        
        NestedMapUtil.put(new DotPath("array"), Arrays.asList(testMap1, testMap2, testMap3), testMap);

        IndexConfig config = store.getConfig("test2");
        GenericTransformer trans = new GenericTransformer();
        trans.transform(config, testMap);
        Assert.assertEquals(2, NestedMapUtil.get(new DotPath("body.test"), testMap));
    }
}
