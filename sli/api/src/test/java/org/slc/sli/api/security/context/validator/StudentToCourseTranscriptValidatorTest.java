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
package org.slc.sli.api.security.context.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class StudentToCourseTranscriptValidatorTest {
    
    @Autowired
    StudentToCourseTranscriptValidator underTest;
    
    @Autowired
    StudentToSubStudentValidator subValidator;
    
    @Autowired
    private PagingRepositoryDelegate<Entity> repo;
    
    @Autowired
    private SecurityContextInjector injector;

    Entity student1 = null;
    Entity student2 = null;
    
    Entity studentAcademicRecord1 = null;
    Entity studentAcademicRecord2 = null;
    
    Entity courseTranscript1 = null;
    Entity courseTranscript2 = null;

    @Before
    public void setUp() throws Exception {
        Map<String, Object> body = new HashMap<String, Object>();
        body = new HashMap<String, Object>();
        body.put("studentUniqueStateId", "1234");
        student1 = repo.create(EntityNames.STUDENT, body);
        
        body = new HashMap<String, Object>();
        body.put("studentUniqueStateId", "4567");
        student2 = repo.create(EntityNames.STUDENT, body);

        body = new HashMap<String, Object>();
        body.put("studentId", student1.getEntityId());
        studentAcademicRecord1 = repo.create(EntityNames.STUDENT_ACADEMIC_RECORD, body);
        
        body = new HashMap<String, Object>();
        body.put("studentId", student2.getEntityId());
        studentAcademicRecord2 = repo.create(EntityNames.STUDENT_ACADEMIC_RECORD, body);
        
        body = new HashMap<String, Object>();
        body.put("studentAcademicRecordId", studentAcademicRecord1.getEntityId());
        courseTranscript1 = repo.create(EntityNames.COURSE_TRANSCRIPT, body);
        
        body = new HashMap<String, Object>();
        body.put("studentAcademicRecordId", studentAcademicRecord2.getEntityId());
        courseTranscript2 = repo.create(EntityNames.COURSE_TRANSCRIPT, body);
    }
    
    @Test
    public void canOnlyValidateCourseTranscriptForStudent() {
        injector.setStudentContext(student1);
        assertTrue(underTest.canValidate(EntityNames.COURSE_TRANSCRIPT, true));
        assertTrue(underTest.canValidate(EntityNames.COURSE_TRANSCRIPT, false));
        
        injector.setStaffContext();
        assertFalse(underTest.canValidate(EntityNames.COURSE_TRANSCRIPT, true));
        assertFalse(underTest.canValidate(EntityNames.COURSE_TRANSCRIPT, false));
    }
    
    @Test
    public void shouldSeeOwnCourseTranscriptsOnly() {
        injector.setStudentContext(student1);
        assertFalse(underTest.validate(EntityNames.COURSE_TRANSCRIPT, new HashSet<String>(Arrays.asList(courseTranscript1.getEntityId()))).isEmpty());
        assertTrue(underTest.validate(EntityNames.COURSE_TRANSCRIPT, new HashSet<String>(Arrays.asList(courseTranscript2.getEntityId()))).isEmpty());
    }
    
}
