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

/**
 * Tests the paging repository delegate.
 * 
 * @author kmyers
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class PagingRepositoryDelegateTest {
    @Autowired
    private PagingRepositoryDelegate<Entity> delegate;
    
    @Autowired
    @Value("${sli.api.security.context.paging}")
    private int count;

    @Test
    public void testPagingBreakup() {
        // Exact number of IDs as limit
        List<List<String>> brokenIds = delegate.extractBrokenListOfIds(generateIds(count));
        assertEquals(brokenIds.size(), 1);
        // Just one over
        brokenIds = delegate.extractBrokenListOfIds(generateIds(count + 1));
        assertEquals(brokenIds.size(), 2);
        assertEquals(brokenIds.get(1).size(), 1);
        assertEquals((String) brokenIds.get(1).get(0), "" + count);
        // Just one under
        brokenIds = delegate.extractBrokenListOfIds(generateIds(count - 1));
        assertEquals(brokenIds.size(), 1);
        assertEquals(brokenIds.get(0).size(), count - 1);
        // Middle case
        brokenIds = delegate.extractBrokenListOfIds(generateIds(count / 2));
        assertEquals(brokenIds.size(), 1);
        assertEquals(brokenIds.get(0).size(), count / 2);
        // Middle case w/ paging
        brokenIds = delegate.extractBrokenListOfIds(generateIds(count * 3 + count / 2));
        assertEquals(brokenIds.size(), 4);
        assertEquals(brokenIds.get(3).size(), count / 2);
    }
    
    private List<String> generateIds(int count) {
        List<String> ids = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            ids.add("" + i);
        }
        return ids;
    }
}
