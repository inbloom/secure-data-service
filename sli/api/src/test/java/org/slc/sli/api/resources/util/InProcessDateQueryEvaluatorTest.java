/*
 * Copyright 2013 Shared Learning Collaborative, LLC
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

package org.slc.sli.api.resources.util;

import junit.framework.Assert;

import org.junit.Test;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * @author: sashton
 */
public class InProcessDateQueryEvaluatorTest {
    
    InProcessDateQueryEvaluator eval = new InProcessDateQueryEvaluator();
    
    @Test
    public void shouldVerifyMissingFieldReturnsFalse() {
        EntityBody entity = new EntityBody();
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("foo", NeutralCriteria.CRITERIA_EXISTS, true));
        
        boolean result = eval.entitySatisfiesDateQuery(entity, query);
        Assert.assertEquals("Should match", false, result);
    }
    
    @Test
    public void shouldVerifyLessThanReturnsTrue() {
        testDateComparison("2005", "2006", NeutralCriteria.CRITERIA_LT, true);
    }
    
    @Test
    public void shouldVerifyLessThanReturnsFalse() {
        testDateComparison("2005", "2005", NeutralCriteria.CRITERIA_LT, false);
    }
    
    @Test
    public void shouldVerifyLteReturnsTrue() {
        testDateComparison("2005", "2005", NeutralCriteria.CRITERIA_LTE, true);
    }
    
    @Test
    public void shouldVerifyLteReturnsFalse() {
        testDateComparison("2005", "2004", NeutralCriteria.CRITERIA_LTE, false);
    }
    
    @Test
    public void shouldVerifyGtReturnsTrue() {
        testDateComparison("2005", "2004", NeutralCriteria.CRITERIA_GT, true);
    }
    
    @Test
    public void shouldVerifyGtReturnsFalse() {
        testDateComparison("2005", "2006", NeutralCriteria.CRITERIA_GT, false);
    }
    
    @Test
    public void shouldVerifyGteReturnsTrue() {
        testDateComparison("2004", "2004", NeutralCriteria.CRITERIA_GTE, true);
    }
    
    @Test
    public void shouldVerifyGteReturnsFalse() {
        testDateComparison("2005", "2006", NeutralCriteria.CRITERIA_GTE, false);
    }
    
    private void testDateComparison(String entityDate, String queryDate, String op, boolean expected) {
        EntityBody entity = new EntityBody();
        entity.put("date", entityDate);
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("date", op, queryDate));
        
        boolean result = eval.entitySatisfiesDateQuery(entity, query);
        Assert.assertEquals("Should match", expected, result);
        
    }

    @Test
    public void shouldVerifyOrQueryReturnsFalse(){

        EntityBody entity = new EntityBody();
        entity.put("date", "2005");
        NeutralQuery query = new NeutralQuery();
        query.addOrQuery(createQuery(new NeutralCriteria("date",NeutralCriteria.CRITERIA_GT,"2007")));


        boolean result = eval.entitySatisfiesDateQuery(entity, query);
        Assert.assertEquals("Should match", false, result);
    }

    @Test
    public void shouldVerifyOrQueryReturnsTrue(){

        EntityBody entity = new EntityBody();
        entity.put("date", "2005");
        NeutralQuery query = new NeutralQuery();
        query.addOrQuery(createQuery(new NeutralCriteria("date",NeutralCriteria.CRITERIA_GT,"2007")));
        query.addOrQuery(createQuery(new NeutralCriteria("date",NeutralCriteria.CRITERIA_GT,"2001")));


        boolean result = eval.entitySatisfiesDateQuery(entity, query);
        Assert.assertEquals("Should match", true, result);
    }

    private NeutralQuery createQuery(NeutralCriteria criteria) {
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(criteria);
        return query;
    }




}
