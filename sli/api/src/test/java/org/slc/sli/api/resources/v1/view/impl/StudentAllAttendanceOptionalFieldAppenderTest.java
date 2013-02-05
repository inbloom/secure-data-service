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


package org.slc.sli.api.resources.v1.view.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.service.MockRepo;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Unit tests
 * @author srupasinghe
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StudentAllAttendanceOptionalFieldAppenderTest {
    private static final String STUDENT_ID = "1234";
    private static final String SCHOOL_ID = "5678";
    
    @Autowired
    private StudentAllAttendanceOptionalFieldAppender studentAllAttendanceOptionalFieldAppender;

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    MockRepo repo;

    private Entity student1Entity;
    private Entity student2Entity;
    private Entity school1Entity;
    private Entity school2Entity;
    private List<Map<String, Object>> attendance1;
    private List<Map<String, Object>> attendance2;
    private List<Map<String, Object>> attendance3;

    @Before
    public void setup() {
        repo.deleteAll();

        // inject administrator security context for unit testing
        injector.setAdminContextWithElevatedRights();

        //create the student entities
        student1Entity = repo.create("student", createTestStudentEntity());
        student2Entity = repo.create("student", createTestStudentEntity());
        
        // create the school entity
        school1Entity = repo.create("school", createTestSchoolEntity());
        school2Entity = repo.create("school", createTestSchoolEntity());
        
        // create attendance events for students
        String schoolYear = "2010-2011";
        Map<String, Object> event1 = new HashMap<String, Object>();
        event1.put("date", "2011-09-12");
        event1.put("event", "In Attendance");
        Map<String, Object> event2 = new HashMap<String, Object>();
        event2.put("date", "2011-09-13");
        event2.put("event", "Tardy");
        event2.put("reason", "Missed school bus");
        Map<String, Object> event3 = new HashMap<String, Object>();
        event3.put("date", "2011-09-14");
        event3.put("event", "In Attendance");
        Map<String, Object> event4 = new HashMap<String, Object>();
        event4.put("date", "2011-09-12");
        event4.put("event", "In Attendance");
        Map<String, Object> event5 = new HashMap<String, Object>();
        event5.put("date", "2011-09-13");
        event5.put("event", "In Attendance");
        Map<String, Object> event6 = new HashMap<String, Object>();
        event6.put("date", "2011-09-14");
        event6.put("event", "Tardy");
        event6.put("reason", "Missed school bus");
        Map<String, Object> event7 = new HashMap<String, Object>();
        event7.put("date", "2011-09-12");
        event7.put("event", "In Attendance");
        Map<String, Object> event8 = new HashMap<String, Object>();
        event8.put("date", "2011-09-13");
        event8.put("event", "In Attendance");
        Map<String, Object> event9 = new HashMap<String, Object>();
        event9.put("date", "2011-09-14");
        event9.put("event", "Tardy");
        event9.put("reason", "Missed school bus");
        
        attendance1 = new ArrayList<Map<String, Object>>();
        Map<String, Object> attendance1Map = new HashMap<String, Object>();
        List<Map<String, Object>> events1 = new ArrayList<Map<String, Object>>();
        events1.add(event1);
        events1.add(event2);
        events1.add(event3);
        attendance1Map.put("schoolYear", schoolYear);
        attendance1Map.put("attendanceEvent", events1);
        attendance1.add(attendance1Map);
        
        attendance2 = new ArrayList<Map<String, Object>>();
        Map<String, Object> attendance2Map = new HashMap<String, Object>();
        List<Map<String, Object>> events2 = new ArrayList<Map<String, Object>>();
        events2.add(event4);
        events2.add(event5);
        events2.add(event6);
        attendance2Map.put("schoolYear", schoolYear);
        attendance2Map.put("attendanceEvent", events2);
        attendance2.add(attendance2Map);
        
        attendance3 = new ArrayList<Map<String, Object>>();
        Map<String, Object> attendance3Map = new HashMap<String, Object>();
        List<Map<String, Object>> events3 = new ArrayList<Map<String, Object>>();
        events3.add(event4);
        events3.add(event5);
        events3.add(event6);
        attendance3Map.put("schoolYear", schoolYear);
        attendance3Map.put("attendanceEvent", events3);
        attendance3.add(attendance3Map);
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setupMockForApplyOptionalField() {
        repo.create("attendance", createAttendance(student1Entity.getEntityId(), school1Entity.getEntityId(), attendance1));
        repo.create("attendance", createAttendance(student2Entity.getEntityId(), school1Entity.getEntityId(), attendance2));
        repo.create("attendance", createAttendance(student1Entity.getEntityId(), school2Entity.getEntityId(), attendance3));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testApplyOptionalField() {
        setupMockForApplyOptionalField();

        List<EntityBody> entities = new ArrayList<EntityBody>();
        entities.add(makeEntityBody(student1Entity));
        entities.add(makeEntityBody(student2Entity));

        entities = studentAllAttendanceOptionalFieldAppender.applyOptionalField(entities, null);

        assertEquals("Should be 2", 2, entities.size());
        assertNotNull("Should not be null", entities.get(0).get("attendances"));

        List<EntityBody> attendances1 = (List<EntityBody>) ((EntityBody) entities.get(0).get("attendances")).get("attendances");
        assertEquals("Should match", 6, attendances1.size());
        
        List<EntityBody> attendances2 = (List<EntityBody>) ((EntityBody) entities.get(1).get("attendances")).get("attendances");
        assertEquals("Should match", 3, attendances2.size());
    }

    private EntityBody makeEntityBody(Entity entity) {
        EntityBody body = new EntityBody();
        body.putAll(entity.getBody());
        body.put("id", entity.getEntityId());
        return body;
    }

    private Map<String, Object> createTestStudentEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("field2", 2);
        entity.put("studentUniqueStateId", STUDENT_ID);
        return entity;
    }
    
    private Map<String, Object> createTestSchoolEntity() {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("stateOrganizationId", SCHOOL_ID);
        return entity;
    }

    private Map<String, Object> createAttendance(String studentId, String schoolId, List<Map<String, Object>> schoolYearAttendance) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("studentId", studentId);
        entity.put("schoolId", schoolId);
        entity.put("schoolYearAttendance", schoolYearAttendance);
        return entity;
    }

}
