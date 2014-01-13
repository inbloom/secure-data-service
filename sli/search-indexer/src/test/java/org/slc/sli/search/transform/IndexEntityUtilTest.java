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
    private static final String NEW_LINE = "\n";
    @Test
    public void testUtil() {
        Map<String, Object> ht = new HashMap<String, Object>();
        ht.put("x", "y");
        IndexEntity ie = new IndexEntity(Action.QUICK_UPDATE, "test", "type", "1", ht);
        Assert.assertEquals("{\"params\":{\"x\":\"y\"},\"script\":\"ctx._source.x=x;\"}", IndexEntityUtil.toUpdateJson(ie));
        
        List<IndexEntity> docs = new ArrayList<IndexEntity>();
        docs.add(new IndexEntity(Action.INDEX, "test", "type", "1", ht));
        docs.add(new IndexEntity(Action.INDEX, "test1", "type2", "2", ht));
        Assert.assertEquals("{\"docs\": [{\"_index\":\"test\", \"_type\":\"type\",\"_id\":\"1\"}," + NEW_LINE + 
                            "{\"_index\":\"test1\", \"_type\":\"type2\",\"_id\":\"2\"}" + NEW_LINE + "]}", 
            IndexEntityUtil.getBulkGetJson(docs));
        
        docs = new ArrayList<IndexEntity>();
        docs.add(new IndexEntity(Action.DELETE, "test", "type", "1", ht));
        docs.add(new IndexEntity(Action.DELETE, "test1", "type2", "2", ht));
        Assert.assertEquals("{\"delete\":{\"_index\":\"test\", \"_type\":\"type\",\"_id\":\"1\"}}" + NEW_LINE + 
                            "{\"delete\":{\"_index\":\"test1\", \"_type\":\"type2\",\"_id\":\"2\"}}" + NEW_LINE, 
            IndexEntityUtil.getBulkDeleteJson(docs));
    }
}
