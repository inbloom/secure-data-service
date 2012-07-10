/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

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
    
    @Autowired
    private SecurityContextInjector injector;

    @Test
    public void testApplySecurityCriteria() {
        injector.setAccessAllAdminContext();
        
        SecurityCriteria securityCriteria = new SecurityCriteria();
        securityCriteria.setSecurityCriteria(new NeutralCriteria("key", "in", "value"));

        NeutralQuery query = new NeutralQuery();
        query = securityCriteria.applySecurityCriteria(query);

        
        assertEquals("Should match", 2, query.getOrQueries().size());
//        assertEquals("Should match", securityCriteria, query.getOrQueries().get(0));
//        assertEquals("Should match", "value", query.getCriteria().get(0).getValue());
    }

    @Test
    public void testApplyBothCriteria() {
        SecurityCriteria securityCriteria = new SecurityCriteria();
        securityCriteria.setSecurityCriteria(new NeutralCriteria("key1", "in", "value1"));
        securityCriteria.setBlacklistCriteria(new NeutralCriteria("key2", "nin", "value2"));

        NeutralQuery query = new NeutralQuery();
        query = securityCriteria.applySecurityCriteria(query);

        assertEquals("Should match", 2, query.getCriteria().size());
        assertEquals("Should match", "key1", query.getCriteria().get(0).getKey());
        assertEquals("Should match", "value1", query.getCriteria().get(0).getValue());
        assertEquals("Should match", "key2", query.getCriteria().get(1).getKey());
        assertEquals("Should match", "value2", query.getCriteria().get(1).getValue());
    }
}
