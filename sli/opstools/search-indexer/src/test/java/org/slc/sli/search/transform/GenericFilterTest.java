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
