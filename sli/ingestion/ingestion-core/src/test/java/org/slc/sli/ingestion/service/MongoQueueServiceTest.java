package org.slc.sli.ingestion.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.queue.ItemBuilder;
import org.slc.sli.ingestion.queue.ItemKeys;
import org.slc.sli.ingestion.queue.ItemValues;

/**
*
* @author jshort
*
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class MongoQueueServiceTest {

    @Autowired
    private QueueService queueService;

    @Autowired
    private ItemBuilder itemBuilder;

    @Before
    public void setUp() {
        queueService.clear();
    }

    @Test
    public void testPushAndReserve() {

        Map<String, Object> item = itemBuilder.buildNewItem();
        Map<String, Object> item2 = itemBuilder.buildNewItem();

        queueService.postItem(item);
        queueService.postItem(item2);

        assertEquals(2L, queueService.count());
        Map<String, Object> fetched = queueService.reserveItem();

        assertNotNull(fetched);

        assertEquals(ItemValues.WORKING, fetched.get(ItemKeys.STATE));
    }
}
