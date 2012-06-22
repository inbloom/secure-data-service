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

import org.junit.Test;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

import static junit.framework.Assert.assertEquals;

/**
 * Unit tests
 *
 */
public class SecurityCriteriaTest {

    @Test
    public void testApplySecurityCriteria() {
        SecurityCriteria securityCriteria = new SecurityCriteria();
        securityCriteria.setSecurityCriteria(new NeutralCriteria("key", "in", "value"));

        NeutralQuery query = new NeutralQuery();
        query = securityCriteria.applySecurityCriteria(query);

        assertEquals("Should match", 1, query.getCriteria().size());
        assertEquals("Should match", "key", query.getCriteria().get(0).getKey());
        assertEquals("Should match", "value", query.getCriteria().get(0).getValue());
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
