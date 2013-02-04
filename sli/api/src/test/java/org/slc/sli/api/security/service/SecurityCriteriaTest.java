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


package org.slc.sli.api.security.service;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.ResponseTooLargeException;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Unit tests
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@DirtiesContext
public class SecurityCriteriaTest {
    
    @Value("${sli.security.in_clause_size}")
    private String inClauseSize;

    @Autowired
    private SecurityContextInjector injector;

    @Test
    public void testApplySecurityCriteria() {
        injector.setAccessAllAdminContext();

        SecurityCriteria securityCriteria = new SecurityCriteria();
        securityCriteria.setSecurityCriteria(new NeutralCriteria("key", "in", "value"));

        NeutralQuery query = new NeutralQuery();
        query = securityCriteria.applySecurityCriteria(query);

        assertEquals("Should match", 1, query.getOrQueries().size());
    }

    @Test
    public void testApplyBothCriteria() {
        injector.setAccessAllAdminContext();

        SecurityCriteria securityCriteria = new SecurityCriteria();
        securityCriteria.setSecurityCriteria(new NeutralCriteria("key1", "in", "value1"));

        NeutralQuery query = new NeutralQuery();
        query = securityCriteria.applySecurityCriteria(query);

        assertEquals("Should match", 1, query.getOrQueries().size());
    }
    
    @Test
    public void testGettersAndSetters() {
        SecurityCriteria securityCriteria = new SecurityCriteria();
        securityCriteria.setCollectionName("Waffles");
        assertTrue(securityCriteria.getCollectionName().equals("Waffles"));
    }
    
    @Test(expected = ResponseTooLargeException.class)
    public void testGet413FromLargeSecurity() throws ResponseTooLargeException {
        Entity body = Mockito.mock(Entity.class);
        Mockito.when(body.getType()).thenReturn(EntityNames.TEACHER);
        Mockito.when(body.getEntityId()).thenReturn("-1");
        
        injector.setCustomContext("DerpSir", "Derp sir", "Merple", Arrays.asList("Educator"), body, "111");
        List<String> ids = new ArrayList<String>();
        Long size = Long.parseLong(inClauseSize);
        for (int i = 0; i < size; ++i) {
            ids.add("" + i);
        }
        SecurityCriteria securityCriteria = new SecurityCriteria();
        securityCriteria.setCollectionName("Waffles");
        securityCriteria.setInClauseSize(size);
        securityCriteria.setSecurityCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, ids));
        try {
            securityCriteria.applySecurityCriteria(new NeutralQuery());
            assertTrue(true);
        } catch (ResponseTooLargeException e) {
            fail("Nothing should have been thrown, we are at, but not over the limit");
        }
        ids.add("Boop");
        // One over
        securityCriteria.setSecurityCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, ids));
        securityCriteria.applySecurityCriteria(new NeutralQuery());
    }
}
