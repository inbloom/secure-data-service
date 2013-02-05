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

package org.slc.sli.api.security.service.mangler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.security.service.SecurityCriteria;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Tests the default query mangler (non abstract class).
 * 
 * 
 * @author kmyers
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class DefaultQueryManglerTest {

    
    @Test
    public void testMangleListQuery() {
        DefaultQueryMangler mangler = new DefaultQueryMangler();
        NeutralQuery query = new NeutralQuery();
        NeutralCriteria baseCriteria = new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, Arrays.asList(new String[] {"1", "2", "3"}));
        SecurityCriteria securityCriteria = new SecurityCriteria();
        // Test generic list query
        securityCriteria.setCollectionName("students");
        securityCriteria.setSecurityCriteria(baseCriteria);
        NeutralQuery finalQuery = mangler.mangleQuery(query, baseCriteria);
        assertTrue(finalQuery.getCriteria().size() == 0);
        assertTrue(finalQuery.getOrQueries().size() == 1);
        assertEquals(finalQuery.getOrQueries().get(0).getCriteria().get(0), baseCriteria);
    }
    
    @Test
    public void testMangleSpecificQuery() {
        DefaultQueryMangler mangler = new DefaultQueryMangler();
        NeutralQuery query = new NeutralQuery();
        NeutralCriteria baseCriteria = new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, Arrays.asList(new String[] {"1", "2", "3"}));
        query.addCriteria(baseCriteria);
        NeutralCriteria secureCriteria = new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, Arrays.asList(new String[] {"1", "2"}));
        SecurityCriteria securityCriteria = new SecurityCriteria();
        // Test generic list query
        securityCriteria.setCollectionName("students");
        securityCriteria.setSecurityCriteria(secureCriteria);
        NeutralQuery finalQuery = mangler.mangleQuery(query, secureCriteria);
        assertTrue(finalQuery.getCriteria().size() == 0);
        assertTrue(finalQuery.getOrQueries().size() == 1);
        assertEquals(finalQuery.getOrQueries().get(0).getCriteria().get(0), baseCriteria);
    }
    
    @Test
    public void testMangleSpecificFailedQuery() {
        DefaultQueryMangler mangler = new DefaultQueryMangler();
        NeutralQuery query = new NeutralQuery();
        NeutralCriteria baseCriteria = new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, Arrays.asList(new String[] {"1", "2", "3"}));
        query.addCriteria(baseCriteria);
        NeutralCriteria secureCriteria = new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, Arrays.asList(new String[] {"4", "5"}));
        SecurityCriteria securityCriteria = new SecurityCriteria();
        // Test generic list query
        securityCriteria.setCollectionName("students");
        securityCriteria.setSecurityCriteria(secureCriteria);
        NeutralQuery finalQuery = mangler.mangleQuery(query, secureCriteria);
        assertEquals(finalQuery.getOrQueries().size(), 0);
    }
    
    @Test
    public void testMangleListWithPaging() {
        DefaultQueryMangler mangler = new DefaultQueryMangler();
        NeutralQuery query = new NeutralQuery();
        List<String> totalQuery = buildLargeQuery(100);
        query.setOffset(0);
        query.setLimit(50);
        NeutralCriteria baseCriteria = new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN,
                totalQuery.subList(0, 50));
        NeutralCriteria secureCriteria = new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, totalQuery);
        SecurityCriteria securityCriteria = new SecurityCriteria();
        // Test generic list query
        securityCriteria.setCollectionName("students");
        securityCriteria.setSecurityCriteria(secureCriteria);
        NeutralQuery finalQuery = mangler.mangleQuery(query, secureCriteria);
        assertTrue(finalQuery.getCriteria().size() == 0);
        assertTrue(finalQuery.getOrQueries().size() == 1);
        NeutralCriteria finalCriteria = finalQuery.getOrQueries().get(0).getCriteria().get(0);
        assertEquals(((List) finalCriteria.getValue()).size(), 50);
    }
    
    @Test
    public void testMangleListWithPagingAndQuery() {
        DefaultQueryMangler mangler = new DefaultQueryMangler();
        NeutralQuery query = new NeutralQuery();
        List<String> totalQuery = buildLargeQuery(50);
        query.setOffset(0);
        query.setLimit(50);
        NeutralCriteria baseCriteria = new NeutralCriteria("body.something", NeutralCriteria.OPERATOR_EQUAL,
                "Waffletown");
        query.addCriteria(baseCriteria);
        NeutralCriteria secureCriteria = new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, totalQuery);
        SecurityCriteria securityCriteria = new SecurityCriteria();
        // Test generic list query
        securityCriteria.setCollectionName("students");
        securityCriteria.setSecurityCriteria(secureCriteria);
        NeutralQuery finalQuery = mangler.mangleQuery(query, secureCriteria);
        assertTrue(finalQuery.getCriteria().size() == 1); // Search criteria
        assertTrue(finalQuery.getOrQueries().size() == 1); // Security Criteria
        NeutralCriteria finalCriteria = finalQuery.getOrQueries().get(0).getCriteria().get(0);
        assertEquals(((List) finalCriteria.getValue()).size(), 50);
    }

    private List<String> buildLargeQuery(int size) {
        List<String> ids = new ArrayList<String>();
        for (int i = 0; i < size; ++i) {
            ids.add("" + i);
        }
        return ids;
    }
    
}
