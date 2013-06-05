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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
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
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;

public class DeltaJournalTest {

    @InjectMocks
    private DeltaJournal deltaJournal = new DeltaJournal();

    @Mock
    private MongoTemplate template;

    @Mock
    private UUIDGeneratorStrategy uuidGeneratorStrategy;

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
        when(uuidGeneratorStrategy.generateId()).thenReturn("123_id");
        deltaJournal.setDeltasEnabled(true);
        deltaJournal.afterPropertiesSet();
    }

    @SuppressWarnings("rawtypes")
    private List<Map> buildThirdBatch() throws DecoderException {
        Map<String, Object> e = new HashMap<String, Object>();
        e.put("_id", Hex.decodeHex("ee".toCharArray()));
        e.put("t", 3L);

        List<Map> res = new LinkedList<Map>();
        res.add(e);

        return res;
    }

    @SuppressWarnings("rawtypes")
    private List<Map> buildSecondBatch() throws DecoderException {
        Map<String, Object> c = new HashMap<String, Object>();
        c.put("_id", Hex.decodeHex("cc".toCharArray()));
        c.put("t", 2L);
        Map<String, Object> d = new HashMap<String, Object>();
        d.put("_id", Hex.decodeHex("dd".toCharArray()));
        d.put("t", 2L);

        List<Map> res = new LinkedList<Map>();
        res.add(c);
        res.add(d);

        return res;
    }

    @SuppressWarnings("rawtypes")
    private List<Map> buildFirstBatch() throws DecoderException {
        Map<String, Object> a = new HashMap<String, Object>();
        a.put("_id", Hex.decodeHex("aa".toCharArray()));
        a.put("t", 1L);
        Map<String, Object> b = new HashMap<String, Object>();
        b.put("_id", Hex.decodeHex("bb".toCharArray()));
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
    public void testJournalCollapsedSubDocs() throws DecoderException {
        String assessment1id = "1234567890123456789012345678901234567890";
        String assessment2id = "1234567890123456789012345678901234567892";
        deltaJournal.journal(
                Arrays.asList(assessment1id + "_id1234567890123456789012345678901234567891_id", assessment1id
                        + "_id1234567890123456789012345678901234567892_id", assessment2id
                        + "_id1234567890123456789012345678901234567891_id"), "assessmentItem", false);
        Query q1 = Query.query(Criteria.where("_id").is(Hex.decodeHex(assessment1id.toCharArray())));
        Query q2 = Query.query(Criteria.where("_id").is(Hex.decodeHex(assessment2id.toCharArray())));
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
    public void testRemove() throws DecoderException {
        deltaJournal.setLimit(2);
        deltaJournal.removeDeltaJournals("Midgar", 100);
        verify(template, times(1)).remove(argThat(buildQueryMatcher(getIds(buildFirstBatch()))), eq("deltas"));
        verify(template, times(1)).remove(argThat(buildQueryMatcher(getIds(buildSecondBatch()))), eq("deltas"));
        verify(template, times(1)).remove(argThat(buildQueryMatcher(getIds(buildThirdBatch()))), eq("deltas"));
    }

    @SuppressWarnings("unchecked")
    private Matcher<Query> buildQueryMatcher(final List<byte[]> ids) {
        return new BaseMatcher<Query>() {

            @Override
            public boolean matches(Object arg0) {
                Query q = (Query) arg0;
                Map<String, Object> inClause = (Map<String, Object>) q.getQueryObject().get("_id");
                List<byte[]> idClause = (List<byte[]>) inClause.get("$in");
                for (int i = 0; i < ids.size(); i++) {
                    if (!Hex.encodeHexString(ids.get(i)).equals(Hex.encodeHexString(idClause.get(i)))) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public void describeTo(Description arg0) {
                arg0.appendText("Query matching ids: " + ids);
            }
        };
    }

    @SuppressWarnings("rawtypes")
    private List<byte[]> getIds(List<Map> batch) {
        List<byte[]> res = new ArrayList<byte[]>();
        for (Map<String, Object> item : batch) {
            res.add((byte[]) item.get("_id"));
        }
        return res;
    }

    @Test
    public void testJournalPurge() {
        final long time = new Date().getTime();

        deltaJournal.journalPurge(time);

        BaseMatcher<Update> updateMatcher = new BaseMatcher<Update>() {

            @Override
            public boolean matches(Object arg0) {
                @SuppressWarnings("unchecked")
                Map<String, Object> o = (Map<String, Object>) ((Update) arg0).getUpdateObject().get("$set");
                if (o.get("c").equals("purge") && o.get("t").equals(time)) {
                    return true;
                }
                return false;
            }

            @Override
            public void describeTo(Description arg0) {
                arg0.appendText("Update with 'c' set to 'purge' and 't' set to time of purge");
            }
        };

        verify(template, Mockito.times(1)).upsert(Mockito.any(Query.class), argThat(updateMatcher),
                Mockito.eq("deltas"));
    }

    @Test
    public void testGetByteId() throws DecoderException {
        String id = "1234567890123456789012345678901234567890";
        String superid = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        String extraid = "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb";
        assertEquals(id, Hex.encodeHexString(DeltaJournal.getByteId(id + "_id").get(0)));
        assertEquals(id, Hex.encodeHexString(DeltaJournal.getByteId(superid + "_id" + id + "_id").get(0)));
        assertEquals(superid, Hex.encodeHexString(DeltaJournal.getByteId(superid + "_id" + id + "_id").get(1)));
        assertEquals(superid, Hex.encodeHexString(DeltaJournal.getByteId(extraid + "_id" + superid + "_id" + id + "_id").get(1)));
        assertEquals(extraid, Hex.encodeHexString(DeltaJournal.getByteId(extraid + "_id" + superid + "_id" + id + "_id").get(2)));
    }

    @Test
    public void testGetEntityId() throws DecoderException {
        String id = "1234567890123456789012345678901234567890";
        String superid = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        String extraid = "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb";
        Map<String, Object> delta = new HashMap<String, Object>();
        delta.put("_id", Hex.decodeHex(id.toCharArray()));
        assertEquals(id + "_id", DeltaJournal.getEntityId(delta));
        delta.put("i", Arrays.asList(Hex.decodeHex(superid.toCharArray())));
        assertEquals(superid + "_id" + id + "_id", DeltaJournal.getEntityId(delta));
        delta.put("i", Arrays.asList(Hex.decodeHex(superid.toCharArray()), Hex.decodeHex(extraid.toCharArray())));
        assertEquals(extraid + "_id" + superid + "_id" + id + "_id", DeltaJournal.getEntityId(delta));

    }
}
