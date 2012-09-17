package org.slc.sli.api.security.context;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class PagingRepositoryDelegateTest {
    @Autowired
    private PagingRepositoryDelegate<Entity> delegate;
    
    @Autowired
    @Value("${sli.api.security.context.paging}")
    private int COUNT;

    @Test
    public void testPagingBreakup() {
        // Exact number of IDs as limit
        List<List<String>> brokenIds = delegate.extractBrokenListOfIds(generateIds(COUNT));
        assertEquals(brokenIds.size(), 1);
        // Just one over
        brokenIds = delegate.extractBrokenListOfIds(generateIds(COUNT + 1));
        assertEquals(brokenIds.size(), 2);
        assertEquals(brokenIds.get(1).size(), 1);
        assertEquals((String) brokenIds.get(1).get(0), "" + COUNT);
        // Just one under
        brokenIds = delegate.extractBrokenListOfIds(generateIds(COUNT - 1));
        assertEquals(brokenIds.size(), 1);
        assertEquals(brokenIds.get(0).size(), COUNT - 1);
        // Middle case
        brokenIds = delegate.extractBrokenListOfIds(generateIds(COUNT / 2));
        assertEquals(brokenIds.size(), 1);
        assertEquals(brokenIds.get(0).size(), COUNT / 2);
        // Middle case w/ paging
        brokenIds = delegate.extractBrokenListOfIds(generateIds(COUNT * 3 + COUNT / 2));
        assertEquals(brokenIds.size(), 4);
        assertEquals(brokenIds.get(3).size(), COUNT / 2);
    }
    
    private List<String> generateIds(int count) {
        List<String> ids = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            ids.add("" + i);
        }
        return ids;
    }
}
