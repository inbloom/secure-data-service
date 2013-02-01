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

package org.slc.sli.common.util.entity;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author jstokes
 */
public class EntityManipulatorTest {

    @Test
    public void testStripFields() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("f1", "v1");

        EntityManipulator.removeFields(body, "f1");
        assertTrue(body.get("f1") == null);
    }

    @Test
    public void testWrongExclude() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("f1", "v1");

        EntityManipulator.removeFields(body, "foobar");
        assertTrue(body.get("f1") != null);
    }

    @Test
    public void testMultipleExclude() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("f1", "v1");
        body.put("f2", "v2");
        body.put("f3", "v3");

        List<String> toStrip = Arrays.asList("f1", "f2");
        EntityManipulator.removeFields(body, toStrip);
        assertTrue(body.get("f1") == null);
        assertTrue(body.get("f2") == null);
        assertTrue(body.get("f3") != null);
    }

    @Test
    public void testStripMap() {
        Map<String, Object> body = new HashMap<String, Object>();
        Map<String, Object> embeddedMap = new HashMap<String, Object>();
        embeddedMap.put("ef1", "ev1");
        embeddedMap.put("ef2", "ev2");
        body.put("f1", embeddedMap);

        EntityManipulator.removeFields(body, "f1.ef1");
        @SuppressWarnings("unchecked")
        Map<String, Object> f1 = (Map<String, Object>) body.get("f1");

        assertTrue(f1 != null);
        assertEquals("ev2", f1.get("ef2"));
        assertTrue(f1.get("ef1") == null);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testStripList() {
        //  { "k1" : [
        //    { "ek1" : "ev1", "ek2" : "ev2" },
        //    { "ek1" : "ev1", "ek2" : "ev2" }] }
        Map<String, Object> body = new HashMap<String, Object>();
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        Map<String, Object> em1 = new HashMap<String, Object>();
        em1.put("ek1", "ev1");
        em1.put("ek2", "ev2");
        Map<String, Object> em2 = new HashMap<String, Object>();
        em2.put("ek1", "ev1");
        em2.put("ek2", "ev2");
        mapList.add(em1);
        mapList.add(em2);
        body.put("k1", mapList);

        EntityManipulator.removeFields(body, "k1.ek1");
        assertTrue(body.get("k1") instanceof List);
        List<Map<String, Object>> mapListRemoved = (List<Map<String, Object>>) body.get("k1");
        assertEquals(2, mapListRemoved.size());

        assertEquals("ev2", mapListRemoved.get(0).get("ek2"));
        assertEquals(null, mapListRemoved.get(0).get("ek1"));

        assertEquals("ev2", mapListRemoved.get(1).get("ek2"));
        assertEquals(null, mapListRemoved.get(1).get("ek1"));

        // { "k1" : [ "ev1", "ev2" ], "k2" : "v2" }
        body = new HashMap<String, Object>();
        List<String> values = Arrays.asList("ev1", "ev2");
        body.put("k1", values);
        body.put("k2", "v2");

        EntityManipulator.removeFields(body, "k1");
        assertEquals("v2", body.get("k2"));
        assertFalse(body.containsKey("k1"));

        // { "k1" : [
        //   "ek1" : [
        //     { "eek1" : "eev1", "eek2" : "eek2" } ]]}

        body = new HashMap<String, Object>();
        List<Object> el1 = new ArrayList<Object>();
        List<Object> el2 = new ArrayList<Object>();
        em2 = new HashMap<String, Object>();
        em2.put("eek1", "eev1");
        em2.put("eek2", "eev2");
        el2.add(em2);
        em1 = new HashMap<String, Object>();
        em1.put("ek1", el2);
        el1.add(em1);
        body.put("k1", el1);

        EntityManipulator.removeFields(body, "k1.ek1.eek1");
        List<Object> k1List = (List<Object>) body.get("k1");
        Map<String, Object> ek1Map = (Map<String, Object>) k1List.get(0);
        List<Object> ek1List = (List<Object>) ek1Map.get("ek1");
        Map<String, Object> removedMap = (Map<String, Object>) ek1List.get(0);

        assertTrue(removedMap.containsKey("eek2"));
        assertEquals("eev2", removedMap.get("eek2"));
        assertEquals(null, removedMap.get("eek1"));
    }
}
