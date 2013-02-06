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
