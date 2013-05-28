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
package org.slc.sli.dal.repository;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import org.slc.sli.common.util.tenantdb.TenantContext;

public class DeltaJournalTest {

    @InjectMocks
    private DeltaJournal deltaJournal = new DeltaJournal();

    @Mock
    private MongoTemplate template;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Before
    public void setUp() throws Exception {
        template = Mockito.mock(MongoTemplate.class);
        MockitoAnnotations.initMocks(this);

        List<Map> firstBatch = buildFirstBatch();
        List<Map> secondBatch = buildSecondBatch();
        List<Map> thirdBatch = buildThirdBatch();
        when(template.find(any(Query.class), (Class<Map>) any(), anyString())).thenReturn(firstBatch)
                .thenReturn(secondBatch).thenReturn(thirdBatch).thenReturn(new ArrayList());
        deltaJournal.setDeltasEnabled(true);
        deltaJournal.afterPropertiesSet();
    }

    @SuppressWarnings("rawtypes")
    private List<Map> buildThirdBatch() {
        Map<String, Object> e = new HashMap<String, Object>();
        e.put("_id", "e");
        e.put("t", 3L);

        List<Map> res = new LinkedList<Map>();
        res.add(e);

        return res;
    }

    @SuppressWarnings("rawtypes")
    private List<Map> buildSecondBatch() {
        Map<String, Object> c = new HashMap<String, Object>();
        c.put("_id", "c");
        c.put("t", 2L);
        Map<String, Object> d = new HashMap<String, Object>();
        d.put("_id", "d");
        d.put("t", 2L);

        List<Map> res = new LinkedList<Map>();
        res.add(c);
        res.add(d);

        return res;
    }

    @SuppressWarnings("rawtypes")
    private List<Map> buildFirstBatch() {
        Map<String, Object> a = new HashMap<String, Object>();
        a.put("_id", "a");
        a.put("t", 1L);
        Map<String, Object> b = new HashMap<String, Object>();
        b.put("_id", "b");
        b.put("t", 2L);

        List<Map> res = new LinkedList<Map>();
        res.add(a);
        res.add(b);

        return res;
    }

    @Test
    public void pagedJournalShouldNotMissAnything() {
        deltaJournal.setLimit(2);
        Iterator<Map<String, Object>> it = deltaJournal.findDeltaRecordBetween(0, 100);
        int count = 0;
        while (it.hasNext()) {
            it.next();
            count++;
        }
        assertTrue(count == 5);
    }
    
    @Test
    public void testIgnoredOnSystemCall() {
        TenantContext.setIsSystemCall(true);
        deltaJournal.journal("test", "userSession", false);
        verify(template, never()).upsert(any(Query.class), any(Update.class), anyString());
        TenantContext.setIsSystemCall(false);
    }

    @Test
    public void testJournalCollapsedSubDocs() {
        deltaJournal.journal(Arrays.asList("assessment_idAssessmentItem1_id", "assessment_idAssessmentItem2_id",
                "assessment2_idAssessmentItem_id"), "assessmentItem", false);
        Query q1 = Query.query(Criteria.where("_id").is("assessment_id"));
        Query q2 = Query.query(Criteria.where("_id").is("assessment2_id"));
        BaseMatcher<Update> updateMatcher = new BaseMatcher<Update>() {

            @Override
            public boolean matches(Object arg0) {
                @SuppressWarnings("unchecked")
                Map<String, Object> o = (Map<String, Object>) ((Update) arg0).getUpdateObject().get("$set");
                return o.get("c").equals("assessment");
            }

            @Override
            public void describeTo(Description arg0) {
                arg0.appendText("Update with 'c' set to 'assessment'");
            }
        };
        verify(template).upsert(eq(q1), argThat(updateMatcher), eq("deltas"));
        verify(template).upsert(eq(q2), argThat(updateMatcher), eq("deltas"));
    }
    
    @Test
    public void testRemove() {
        deltaJournal.setLimit(2);
        deltaJournal.removeDeltaJournals("Midgar", 100);
        Query removeIds = new Query(where("_id").in(getIds(buildFirstBatch())));
        verify(template, times(1)).remove(removeIds, "deltas");
        removeIds = new Query(where("_id").in(getIds(buildSecondBatch())));
        verify(template, times(1)).remove(removeIds, "deltas");
        removeIds = new Query(where("_id").in(getIds(buildThirdBatch())));
        verify(template, times(1)).remove(removeIds, "deltas");
    }

    @SuppressWarnings("rawtypes")
    private List<String> getIds(List<Map> batch) {
        List<String> res = new ArrayList<String>();
        for (Map<String, Object> item : batch) {
            res.add((String) item.get("_id"));
        }
        return res;
    }

}
