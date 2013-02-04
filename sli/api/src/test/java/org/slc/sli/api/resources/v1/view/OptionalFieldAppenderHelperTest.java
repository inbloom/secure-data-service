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


package org.slc.sli.api.resources.v1.view;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.service.MockRepo;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Unit tests
 * @author srupasinghe
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class OptionalFieldAppenderHelperTest {

    @Autowired
    OptionalFieldAppenderHelper helper;

    @Autowired
    MockRepo repo;

    @Autowired
    private SecurityContextInjector injector;

    @Before
    public void setup() {
        // inject administrator security context for unit testing
        injector.setAdminContextWithElevatedRights();
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testGetEntityFromList() {
        EntityBody body = helper.getEntityFromList(createEntityList(true), "field1", "2");
        assertEquals("Should match", "2", body.get("field1"));
        assertEquals("Should match", "2", body.get("field2"));
        assertEquals("Should match", "2", body.get("id"));

        body =  helper.getEntityFromList(null, "field1", "2");
        assertNull("Should be null", body);

        body =  helper.getEntityFromList(createEntityList(true), null, "2");
        assertNull("Should be null", body);

        body =  helper.getEntityFromList(createEntityList(true), "field1", null);
        assertNull("Should be null", body);

        body =  helper.getEntityFromList(null, null, null);
        assertNull("Should be null", body);

        body =  helper.getEntityFromList(createEntityList(true), "", "");
        assertNull("Should be null", body);
    }

    @Test
    public void testGetEntitySubList() {
        List<EntityBody> list = helper.getEntitySubList(createEntityList(true), "field1", "3");
        assertEquals("Should match", 2, list.size());
        assertEquals("Should match", "3", list.get(0).get("field1"));
        assertEquals("Should match", "3", list.get(1).get("field1"));

        list = helper.getEntitySubList(createEntityList(true), "field1", "0");
        assertEquals("Should match", 0, list.size());

        list =  helper.getEntitySubList(null, "field1", "2");
        assertEquals("Should match", 0, list.size());

        list =  helper.getEntitySubList(createEntityList(true), null, "2");
        assertEquals("Should match", 0, list.size());

        list =  helper.getEntitySubList(createEntityList(true), "field1", null);
        assertEquals("Should match", 0, list.size());

        list =  helper.getEntitySubList(null, null, null);
        assertEquals("Should match", 0, list.size());

        list =  helper.getEntitySubList(createEntityList(true), "", "");
        assertEquals("Should match", 0, list.size());
    }

    @Test
    public void testGetIdList() {
        List<String> list = helper.getIdList(createEntityList(true), "id");

        assertEquals("Should match", 4, list.size());
        assertTrue("Should contain", list.contains("1"));
        assertTrue("Should contain", list.contains("2"));
        assertTrue("Should contain", list.contains("3"));
        assertTrue("Should contain", list.contains("4"));
        assertFalse("Should not contain", list.contains("5"));

        list =  helper.getIdList(null, "id");
        assertEquals("Should match", 0, list.size());

        list =  helper.getIdList(null, null);
        assertEquals("Should match", 0, list.size());

        list =  helper.getIdList(createEntityList(true), "");
        assertEquals("Should match", 0, list.size());
    }

    @Test
    public void testSectionIds() {
        Set<String> list = helper.getSectionIds(createEntityList(true));

        assertEquals("Should match", 4, list.size());
        assertTrue("Should be true", list.contains("1"));
        assertTrue("Should be true", list.contains("2"));
        assertTrue("Should be true", list.contains("3"));
        assertTrue("Should be true", list.contains("4"));
    }

    @Test
    public void testSectionIdsNoAssociation() {
        Set<String> list = helper.getSectionIds(createEntityList(false));

        assertTrue("List should be empty", list.isEmpty());
    }

    private List<EntityBody> createEntityList(boolean addAssociation) {
        List<EntityBody> list = new ArrayList<EntityBody>();

        list.add(createTestEntity("1", "1", "1", addAssociation));
        list.add(createTestEntity("2", "2", "2", addAssociation));
        list.add(createTestEntity("3", "3", "3", addAssociation));
        list.add(createTestEntity("4", "3", "4", addAssociation));

        return list;
    }

    private EntityBody createTestEntity(String id, String value1, String value2, boolean addAssociation) {
        EntityBody body = new EntityBody();
        body.put("id", id);
        body.put("field1", value1);
        body.put("field2", value2);

        if (addAssociation) {
            EntityBody assoc = new EntityBody();
            assoc.put("sectionId", id);
            assoc.put("targetId", "77");

            List<EntityBody> assocList = new ArrayList<EntityBody>();
            assocList.add(assoc);

            body.put("studentSectionAssociation", assocList);
        }

        return body;
    }

}
