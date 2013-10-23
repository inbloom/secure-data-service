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
package org.slc.sli.bulk.extract.context.resolver.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.slc.sli.bulk.extract.context.resolver.impl.CourseTranscriptContextResolver.ED_ORG_REFERENCE;
import static org.slc.sli.bulk.extract.context.resolver.impl.CourseTranscriptContextResolver.STUDENT_ACADEMIC_RECORD_ID;
import static org.slc.sli.bulk.extract.context.resolver.impl.CourseTranscriptContextResolver.STUDENT_ID;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.Repository;

public class CourseTranscriptContextResolverTest {
    
    @InjectMocks
    CourseTranscriptContextResolver underTest = new CourseTranscriptContextResolver();
    
    @Mock
    Repository<Entity> repo;
    
    @Mock
    StudentContextResolver studentResolver;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(repo.findById(EntityNames.STUDENT_ACADEMIC_RECORD, "studentacademicrecord1")).thenReturn(buildStudentAcademicRecord());
    }
    

    @Test
    public void returnEmptySetIfNoStudent() {
        Entity courseTranscript = buildCourseTranscript();
        courseTranscript.getBody().remove(STUDENT_ID);
        courseTranscript.getBody().remove(STUDENT_ACADEMIC_RECORD_ID);
        assertEquals(Collections.emptySet(), underTest.findGoverningEdOrgs(courseTranscript));
    }
    
    @Test
    public void followStudentId() {
        Entity courseTranscript = buildCourseTranscript();
        when(studentResolver.findGoverningEdOrgs("student1", courseTranscript)).thenReturn(new HashSet<String>(Arrays.asList("toplea2")));
        assertEquals(new HashSet<String>(Arrays.asList("toplea2")), underTest.findGoverningEdOrgs(courseTranscript));
    }
    
    @Test
    public void noStudentIdFollowAcademicRecord() {
        Entity courseTranscript = buildCourseTranscript();
        courseTranscript.getBody().remove(STUDENT_ID);
        when(studentResolver.findGoverningEdOrgs("student1", courseTranscript)).thenReturn(new HashSet<String>(Arrays.asList("toplea2")));
        assertEquals(new HashSet<String>(Arrays.asList("toplea2")), underTest.findGoverningEdOrgs(courseTranscript));
    }

    @Test
    public void testDifferentEdOrgs() {
        Entity courseTranscript = buildCourseTranscript();
        Entity studentAcademicRecord = buildStudentAcademicRecord();
        studentAcademicRecord.getBody().put(STUDENT_ID, "student2");
        when(repo.findById(EntityNames.STUDENT_ACADEMIC_RECORD, "studentacademicrecord1")).thenReturn(studentAcademicRecord);

        when(studentResolver.findGoverningEdOrgs("student1", courseTranscript)).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        when(studentResolver.findGoverningEdOrgs("student2", courseTranscript)).thenReturn(new HashSet<String>(Arrays.asList("edOrg2")));

        assertEquals(new HashSet<String>(Arrays.asList("edOrg1", "edOrg2")), underTest.findGoverningEdOrgs(courseTranscript));
    }

    private Entity buildCourseTranscript() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ED_ORG_REFERENCE, Arrays.asList("lea1"));
        body.put(STUDENT_ID, "student1");
        body.put(STUDENT_ACADEMIC_RECORD_ID, "studentacademicrecord1");
        return new MongoEntity(EntityNames.COURSE_TRANSCRIPT, "coursetranscript123", body, Collections.<String, Object> emptyMap());
    }
    
    private Entity buildStudentAcademicRecord() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(STUDENT_ID, "student1");
        return new MongoEntity(EntityNames.STUDENT_ACADEMIC_RECORD, "studentacademicrecord1", body, Collections.<String, Object> emptyMap());
    }
}
