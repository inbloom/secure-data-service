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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;

/**
 * JUnits for Entity Encoder
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class EntityEncoderTest {

    // Test subject
    @Resource(name = "entityKeyEncoder")
    private EntityKeyEncoder entityKeyEncoder;

    @SuppressWarnings("unchecked")
    @Test
    public void testEncodeEntityKey() {
        Map<String, Object> body = new HashMap<String, Object>();
        Map<String, Object> embedded = new HashMap<String, Object>();
        Map<String, Object> inlist1 = new HashMap<String, Object>();
        Map<String, Object> inlist2 = new HashMap<String, Object>();

        body.put("normal-key", "normal-value");
        body.put("period.key", "period.value");
        body.put("percent%key", "percent%value");
        body.put("both.%.%key", "both.%.%value");

        inlist1.put("inlist1", "1");
        inlist2.put("inlist", "inlist");
        inlist2.put("in.list", "in.list");
        inlist2.put("period.percentage%", "period.percentage%");
        List<Object> list = new ArrayList<Object>();
        list.add(inlist1);
        list.add(inlist2);

        embedded.put("normal-key-embedded", "normal-key-embedded");
        embedded.put("period.key-embedded", "period.value-embedded");
        embedded.put("percent%key-embedded", "percent%value-embedded");
        embedded.put("both.%.%key-embedded", "both.%.%value-embedded");
        embedded.put("list", list);

        body.put("embedded.key", embedded);

        Entity e = new MongoEntity("dummy", body);

        entityKeyEncoder.encodeEntityKey(e);

        body = e.getBody();

        assertTrue(body.containsKey("normal-key"));
        assertEquals("normal-value", body.get("normal-key"));
        assertTrue(body.containsKey("period%2Ekey"));
        assertEquals("period.value", body.get("period%2Ekey"));
        assertTrue(body.containsKey("percent%25key"));
        assertEquals("percent%value", body.get("percent%25key"));
        assertTrue(body.containsKey("both%2E%25%2E%25key"));
        assertEquals("both.%.%value", body.get("both%2E%25%2E%25key"));

        assertTrue(body.containsKey("embedded%2Ekey"));
        embedded = (Map<String, Object>) body.get("embedded%2Ekey");
        assertTrue(embedded.containsKey("normal-key-embedded"));
        assertEquals("normal-key-embedded", embedded.get("normal-key-embedded"));
        assertTrue(embedded.containsKey("period%2Ekey-embedded"));
        assertEquals("period.value-embedded", embedded.get("period%2Ekey-embedded"));
        assertTrue(embedded.containsKey("percent%25key-embedded"));
        assertEquals("percent%value-embedded", embedded.get("percent%25key-embedded"));
        assertTrue(embedded.containsKey("both%2E%25%2E%25key-embedded"));
        assertEquals("both.%.%value-embedded", embedded.get("both%2E%25%2E%25key-embedded"));

        assertTrue(embedded.containsKey("list"));
        list = (List<Object>) embedded.get("list");
        assertNotNull(list);
        assertEquals(2, list.size());
        inlist1 = (Map<String, Object>) list.get(0);
        inlist2 = (Map<String, Object>) list.get(1);
        assertTrue(inlist1.containsKey("inlist1"));
        assertEquals("1", inlist1.get("inlist1"));
        assertTrue(inlist2.containsKey("inlist"));
        assertEquals("inlist", inlist2.get("inlist"));
        assertTrue(inlist2.containsKey("in%2Elist"));
        assertEquals("in.list", inlist2.get("in%2Elist"));
        assertTrue(inlist2.containsKey("period%2Epercentage%25"));
        assertEquals("period.percentage%", inlist2.get("period%2Epercentage%25"));

    }

}
