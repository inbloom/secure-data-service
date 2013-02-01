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
package org.slc.sli.search.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slc.sli.search.connector.impl.ESOperation;
import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.entity.IndexEntity.Action;
import org.slc.sli.search.process.impl.IndexerImpl;
import org.slc.sli.search.util.MockRestTemplate;
import org.springframework.http.HttpEntity;

public class IndexerTest {
    private final IndexerImpl indexer = new IndexerImpl();
    private final MockRestTemplate searchTemplate = new MockRestTemplate();
    
    @Before
    public void setup() {
        ESOperation searchEngineConnector = new ESOperation();
        searchEngineConnector.setSearchTemplate(searchTemplate);
        indexer.setBulkSize(1);
        indexer.setSearchEngineConnector(searchEngineConnector);
        indexer.setAggregatePeriod(500);
        indexer.setIndexerWorkerPoolSize(2);
        searchEngineConnector.setSearchUrl("");
        indexer.init();
        indexer.setAggregatePeriod(10);
        searchTemplate.reset();
    }
    
    @After
    public void destroy() {
        indexer.destroy();
    }
    
    @Test
    public void testIndexing() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("body", 1);
        indexer.index(new IndexEntity("tests", "test", "1", map));
        indexer.flushIndexQueue();
        List<HttpEntity<?>> calls = searchTemplate.getCalls();
        for (HttpEntity<?> entity : calls) {
            if ("{\"index\":{\"_index\":\"tests\", \"_type\":\"test\",\"_id\":\"1\"}}\n{\"body\":1}\n".equals(entity.getBody())) {
                return;
            }
        }
        Assert.fail("Must find the indexed entity in the calls");
    }
    
    @Test
    public void testBulkGetUpdate() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("body", 1);
        List<IndexEntity> ies = new ArrayList<IndexEntity>();
        ies.add(new IndexEntity(Action.UPDATE, "tests", "test", "1", map));
        ies.add(new IndexEntity(Action.UPDATE, "tests1", "test1", "2", map));
        indexer.execute(Action.UPDATE, ies);
        List<HttpEntity<?>> calls = searchTemplate.getCalls();
        // 2 class - _mget and _bulk 
        Assert.assertEquals(2, calls.size());
        Assert.assertEquals("{\"docs\": [{\"_index\":\"tests\", \"_type\":\"test\",\"_id\":\"1\"},\n" +
        		                        "{\"_index\":\"tests1\", \"_type\":\"test1\",\"_id\":\"2\"}\n]}", calls.get(0).getBody());
    }
}
