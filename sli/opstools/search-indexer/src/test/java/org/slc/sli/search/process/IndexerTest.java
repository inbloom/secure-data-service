package org.slc.sli.search.process;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.process.impl.IndexerImpl;
import org.slc.sli.search.transform.IndexEntityConverter;
import org.slc.sli.search.util.MockRestTemplate;
import org.springframework.http.HttpEntity;

public class IndexerTest {
    private IndexerImpl indexer = new IndexerImpl();
    private MockRestTemplate searchTemplate = new MockRestTemplate();
    
    @Before
    public void setup() {
        indexer.setSearchTemplate(searchTemplate);
        indexer.setBulkSize(1);
        indexer.setSearchUrl("");
        indexer.setIndexEntityConverter(new IndexEntityConverter());
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
        indexer.index(new IndexEntity("tests", "test", "1", "{body:1}"));
        indexer.flushQueue();
        List<HttpEntity<?>> calls = searchTemplate.getCalls();
        Assert.assertEquals(1, calls.size());
        Assert.assertEquals("{\"index\":{\"_index\":\"tests\", \"_type\":\"test\",\"_id\":\"1\"}}\n{body:1}\n", calls.get(0).getBody());
    }
}
