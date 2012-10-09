package org.slc.sli.search.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.process.impl.IndexerImpl;
import org.slc.sli.search.util.MockRestTemplate;
import org.springframework.http.HttpEntity;

public class IndexerTest {
    private final IndexerImpl indexer = new IndexerImpl();
    private final MockRestTemplate searchTemplate = new MockRestTemplate();
    
    @Before
    public void setup() {
        indexer.setSearchTemplate(searchTemplate);
        indexer.setBulkSize(1);
        indexer.setSearchUrl("");
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
        Assert.assertEquals(1, calls.size());
        Assert.assertEquals("{\"index\":{\"_index\":\"tests\", \"_type\":\"test\",\"_id\":\"1\"}}\n{\"body\":1}\n", calls.get(0).getBody());
    }
}
