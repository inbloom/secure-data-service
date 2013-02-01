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

package org.slc.sli.api.security.pdp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.ws.rs.core.PathSegment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.context.resolver.SectionHelper;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Tests for UriMutator class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class UriMutatorTest {

    private static final String VERSION = "v1.0";

    @Autowired
    UriMutator mutator;
    
    @Autowired
    @Qualifier("validationRepo")
    Repository<Entity> repo;

    private SectionHelper sectionHelper;
    private EdOrgHelper edOrgHelper;

    private Entity teacher;
    private Entity staff;

    @Before
    public void setup() throws Exception {
        teacher = mock(Entity.class);
        when(teacher.getType()).thenReturn(EntityNames.TEACHER);
        when(teacher.getEntityId()).thenReturn("teacher123");

        staff = mock(Entity.class);
        when(staff.getType()).thenReturn(EntityNames.STAFF);
        when(staff.getEntityId()).thenReturn("staff123");

        sectionHelper = mock(SectionHelper.class);
        when(sectionHelper.getTeachersSections(teacher)).thenReturn(Arrays.asList("section123"));

        edOrgHelper = mock(EdOrgHelper.class);
        when(edOrgHelper.getFilteredDirectEdorgs(teacher)).thenReturn(new HashSet<String>(Arrays.asList("school123")));
        when(edOrgHelper.getFilteredDirectEdorgs(staff)).thenReturn(new HashSet<String>(Arrays.asList("edOrg123")));

        mutator.setSectionHelper(sectionHelper);
        mutator.setEdOrgHelper(edOrgHelper);
    }

    @Test
    public void testV1Mutate() {
        // Testing that we don't crash the api if we do an api/v1/ request
        PathSegment v1 = Mockito.mock(PathSegment.class);
        when(v1.getPath()).thenReturn("v1");
        Assert.assertEquals("Bad endpoint of /v1 is redirected to v1/home safely", createMutatedContainer("/home", ""),
                mutator.mutate(Arrays.asList(v1), null, staff));
        Assert.assertEquals("Bad endpoint of /v1 is redirected to v1/home safely", createMutatedContainer("/home", ""),
                mutator.mutate(Arrays.asList(v1), null, teacher));
    }
    
    @Test
    public void testDeterministicRewrite() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "teacher");
        Entity teacher = repo.create("teacher", body, "staff");
        PathSegment v1 = Mockito.mock(PathSegment.class);
        when(v1.getPath()).thenReturn("/staff");
        Assert.assertEquals("Endponit should be rewritten to /teachers/id",
        		createMutatedContainer("/teachers/" + teacher.getEntityId(), null),
                mutator.mutate(Arrays.asList(v1), "staffUniqueStateId=teacher", null));
        
        body.put("staffUniqueStateId", "staff");
        teacher = repo.create("staff", body, "staff");
        v1 = Mockito.mock(PathSegment.class);
        when(v1.getPath()).thenReturn("/staff");
        Assert.assertEquals("Endponit should be rewritten to /staff/id",
        		createMutatedContainer("/staff/" + teacher.getEntityId(), null),
                mutator.mutate(Arrays.asList(v1), "staffUniqueStateId=staff", null));
    }

    @Test
    public void testGetInferredUrisForTeacher() throws Exception {
        HashMap<String, String> mutatedContentType = new HashMap<String, String>();
        mutatedContentType.put("Content-Type", "application/vnd.slc.search.full+json");

        Assert.assertEquals("inferred uri for teacher resource: /" + ResourceNames.ASSESSMENTS + " is incorrect.",
                createMutatedContainer("/search/assessments", "", mutatedContentType),
                mutator.mutateBaseUri(VERSION, ResourceNames.ASSESSMENTS, "", teacher));

        Assert.assertEquals("inferred uri for teacher resource: /" + ResourceNames.ATTENDANCES + " is incorrect.",
                createMutatedContainer("/sections/section123/studentSectionAssociations/students/attendances", ""),
                mutator.mutateBaseUri(VERSION, ResourceNames.ATTENDANCES, "", teacher));

        Assert.assertEquals("inferred uri for teacher resource: /" + ResourceNames.COMPETENCY_LEVEL_DESCRIPTORS + " is incorrect.",
                createMutatedContainer("/search/competencyLevelDescriptor", "", mutatedContentType),
                mutator.mutateBaseUri(VERSION, ResourceNames.COMPETENCY_LEVEL_DESCRIPTORS, "", teacher));

        Assert.assertEquals("inferred uri for teacher resource: /" + ResourceNames.HOME + " is incorrect.",
                createMutatedContainer("/home", ""),
                mutator.mutateBaseUri(VERSION, ResourceNames.HOME, "", teacher));

        Assert.assertEquals("inferred uri for teacher resource: /" + ResourceNames.LEARNINGSTANDARDS + " is incorrect.",
                createMutatedContainer("/search/learningStandards", "", mutatedContentType),
                mutator.mutateBaseUri(VERSION, ResourceNames.LEARNINGSTANDARDS, "", teacher));

        Assert.assertEquals("inferred uri for teacher resource: /" + ResourceNames.LEARNINGOBJECTIVES + " is incorrect.",
                createMutatedContainer("/search/learningObjectives", "", mutatedContentType),
                mutator.mutateBaseUri(VERSION, ResourceNames.LEARNINGOBJECTIVES, "", teacher));

        Assert.assertEquals("inferred uri for teacher resource: /" + ResourceNames.SECTIONS + " is incorrect.",
                createMutatedContainer("/teachers/teacher123/teacherSectionAssociations/sections", ""),
                mutator.mutateBaseUri(VERSION, ResourceNames.SECTIONS, "", teacher));

        Assert.assertEquals("inferred uri for teacher resource: /" + ResourceNames.STUDENT_SECTION_ASSOCIATIONS + " is incorrect.",
                createMutatedContainer("/sections/section123/studentSectionAssociations", ""),
                mutator.mutateBaseUri(VERSION, ResourceNames.STUDENT_SECTION_ASSOCIATIONS, "", teacher));

        Assert.assertEquals("inferred uri for teacher resource: /" + ResourceNames.TEACHERS + " is incorrect.",
                createMutatedContainer("/schools/school123/teacherSchoolAssociations/teachers", ""),
                mutator.mutateBaseUri(VERSION, ResourceNames.TEACHERS, "", teacher));

        Assert.assertEquals("inferred uri for teacher resource: /" + ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS + " is incorrect.",
                createMutatedContainer("/teachers/teacher123/teacherSchoolAssociations", ""),
                mutator.mutateBaseUri(VERSION, ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS, "", teacher));

        Assert.assertEquals("inferred uri for teacher resource: /" + ResourceNames.TEACHER_SECTION_ASSOCIATIONS + " is incorrect.",
                createMutatedContainer("/teachers/teacher123/teacherSectionAssociations", ""),
                mutator.mutateBaseUri(VERSION, ResourceNames.TEACHER_SECTION_ASSOCIATIONS, "", teacher));
    }

    @Test
    public void testGetInferredUrisForStaff() throws Exception {
        HashMap<String, String> mutatedContentType = new HashMap<String, String>();
        mutatedContentType.put("Content-Type", "application/vnd.slc.search.full+json");

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.ASSESSMENTS + " is incorrect.",
                createMutatedContainer("/search/assessments", "", mutatedContentType),
                mutator.mutateBaseUri(VERSION, ResourceNames.ASSESSMENTS, "", staff));

        Assert.assertEquals("inferred uri for teacher resource: /" + ResourceNames.ATTENDANCES + " is incorrect.",
                createMutatedContainer("/schools/edOrg123/studentSchoolAssociations/students/attendances", ""),
                mutator.mutateBaseUri(VERSION, ResourceNames.ATTENDANCES, "", staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.COHORTS + " is incorrect.",
                createMutatedContainer("/staff/staff123/staffCohortAssociations/cohorts", ""),
                mutator.mutateBaseUri(VERSION, ResourceNames.COHORTS, "", staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.COMPETENCY_LEVEL_DESCRIPTORS + " is incorrect.",
                createMutatedContainer("/search/competencyLevelDescriptor", "", mutatedContentType),
                mutator.mutateBaseUri(VERSION, ResourceNames.COMPETENCY_LEVEL_DESCRIPTORS, "", staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.HOME + " is incorrect.",
                createMutatedContainer("/home", ""),
                mutator.mutateBaseUri(VERSION, ResourceNames.HOME, "", staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.LEARNINGSTANDARDS + " is incorrect.",
                createMutatedContainer("/search/learningStandards", "", mutatedContentType),
                mutator.mutateBaseUri(VERSION, ResourceNames.LEARNINGSTANDARDS, "", staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.LEARNINGOBJECTIVES + " is incorrect.",
                createMutatedContainer("/search/learningObjectives", "", mutatedContentType),
                mutator.mutateBaseUri(VERSION, ResourceNames.LEARNINGOBJECTIVES, "", staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.PROGRAMS + " is incorrect.",
                createMutatedContainer("/staff/staff123/staffProgramAssociations/programs", ""),
                mutator.mutateBaseUri(VERSION, ResourceNames.PROGRAMS, "", staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.SECTIONS + " is incorrect.",
                createMutatedContainer("/schools/edOrg123/sections", ""),
                mutator.mutateBaseUri(VERSION, ResourceNames.SECTIONS, "", staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.STAFF + " is incorrect.",
                createMutatedContainer("/educationOrganizations/edOrg123/staffEducationOrgAssignmentAssociations/staff", ""),
                mutator.mutateBaseUri(VERSION, ResourceNames.STAFF, "", staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.STAFF_COHORT_ASSOCIATIONS + " is incorrect.",
                createMutatedContainer("/staff/staff123/staffCohortAssociations", ""),
                mutator.mutateBaseUri(VERSION, ResourceNames.STAFF_COHORT_ASSOCIATIONS, "", staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS + " is incorrect.",
                createMutatedContainer("/staff/staff123/staffEducationOrgAssignmentAssociations", ""),
                mutator.mutateBaseUri(VERSION, ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS, "", staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.STAFF_PROGRAM_ASSOCIATIONS + " is incorrect.",
                createMutatedContainer("/staff/staff123/staffProgramAssociations", ""),
                mutator.mutateBaseUri(VERSION, ResourceNames.STAFF_PROGRAM_ASSOCIATIONS, "", staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.STUDENTS + " is incorrect.",
                createMutatedContainer("/schools/edOrg123/studentSchoolAssociations/students", ""),
                mutator.mutateBaseUri(VERSION, ResourceNames.STUDENTS, "", staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS + " is incorrect.",
                createMutatedContainer("/schools/edOrg123/studentSchoolAssociations", ""),
                mutator.mutateBaseUri(VERSION, ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, "", staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.TEACHERS + " is incorrect.",
                createMutatedContainer("/schools/edOrg123/teacherSchoolAssociations/teachers", ""),
                mutator.mutateBaseUri(VERSION, ResourceNames.TEACHERS, "", staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS + " is incorrect.",
                createMutatedContainer("/schools/edOrg123/teacherSchoolAssociations", ""),
                mutator.mutateBaseUri(VERSION, ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS, "", staff));
    }

    private MutatedContainer createMutatedContainer(String path, String params) {
        MutatedContainer m = new MutatedContainer();
        m.setPath(path);
        m.setQueryParameters(params);
        return m;
    }

    private MutatedContainer createMutatedContainer(String path, String params, Map<String, String> headers) {
        MutatedContainer m = new MutatedContainer();
        m.setPath(path);
        m.setQueryParameters(params);
        m.setHeaders(headers);
        return m;
    }
}
