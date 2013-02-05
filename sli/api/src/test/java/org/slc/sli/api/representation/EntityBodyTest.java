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


package org.slc.sli.api.representation;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.representation.EmbeddedLink;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.test.WebContextTestExecutionListener;

/**
 * Class to test methods of org.slc.sli.api.representation.EntityBody.
 *
 * @author vmcglaughlin
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class })
public class EntityBodyTest {

    @Test
    public void testGetId() {
        EntityBody entityBody = new EntityBody(createTestMap());

        List<String> list1 = entityBody.getValues("key1");
        assertNotNull("List should not be null", list1);
        assertEquals("List should have 1 value", list1.size(), 1);
        assertEquals("List value should be original id", list1.get(0), "stringValue1");

        List<String> list2 = entityBody.getValues("key2");
        assertNotNull("List should not be null", list2);
        assertEquals("List should have 1 value", list2.size(), 1);
        assertEquals("List value should be original id", list2.get(0), "stringValue2");

        List<String> list3 = entityBody.getValues("key3");
        assertNotNull("List should not be null", list3);
        assertEquals("List should have 2 values", list3.size(), 2);
        assertEquals("List value 1 should be original id", list3.get(0), "stringInList1");
        assertEquals("List value 2 should be original id", list3.get(1), "stringInList2");

        List<String> list4 = entityBody.getValues("key4");
        assertNotNull("List should not be null", list4);
        assertEquals("List should have 0 values", list4.size(), 0);
    }

    private Map<String, Object> createTestMap() {
        Map<String, Object> underlyingMap = new HashMap<String, Object>();

        underlyingMap.put("key1", "stringValue1");
        underlyingMap.put("key2", "stringValue2");

        List<String> list1 = new ArrayList<String>();
        list1.add("stringInList1");
        list1.add("stringInList2");
        underlyingMap.put("key3", list1);

        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("embeddedKey1", "embeddedMapValue1");
        map1.put("embeddedKey2", "embeddedMapValue2");

        underlyingMap.put("key4", map1);

        return underlyingMap;
    }
    
    @Test
    public void embeddedLinkTest() {
        EmbeddedLink link1 = new EmbeddedLink("one", "three");
        EmbeddedLink link2 = new EmbeddedLink("one", "three");
        assertEquals(link1, link2);
        assertEquals(link1.hashCode(), link2.hashCode());
        assertEquals(link1.toString(), link2.toString());
    }
    
    @Test
    public void embeddedLinkTest2() {
        EmbeddedLink link1 = new EmbeddedLink("one", "three");
        EmbeddedLink link2 = new EmbeddedLink("one", null);
        assertFalse(link1.equals(link2));
        assertFalse(link2.equals(link1));
        assertFalse(link1.hashCode() == link2.hashCode());
        assertFalse(link1.toString() == link2.toString());
    }
    
    @Test
    public void embeddedLinkTest3() {
        EmbeddedLink link1 = new EmbeddedLink("one", "three");
        Object o = new Object();
        assertFalse(link1.equals(o));
        assertFalse(link1.equals(null));
    }
}
