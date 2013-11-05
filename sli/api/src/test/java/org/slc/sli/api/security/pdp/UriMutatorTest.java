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

import java.util.*;

import javax.ws.rs.core.PathSegment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.context.resolver.SectionHelper;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 * Tests for UriMutator class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class UriMutatorTest {

    private static final String VERSION = "v1.0";
    private static final String ADMIN_APP_ID = "adminAppId";

    @Autowired
    UriMutator mutator;

    @Autowired
    @Qualifier("validationRepo")
    Repository<Entity> repo;

    private SectionHelper sectionHelper;
    private EdOrgHelper edOrgHelper;

    private Entity teacher;
    private Entity staff;

    private SLIPrincipal principal;
    private Entity app;
    private Entity adminApp;

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

        principal = mock(SLIPrincipal.class);

        edOrgHelper = mock(EdOrgHelper.class);
        when(edOrgHelper.getDirectEdorgs(teacher)).thenReturn(new HashSet<String>(Arrays.asList("school123")));
        when(edOrgHelper.getDirectEdorgs(staff)).thenReturn(new HashSet<String>(Arrays.asList("edOrg123")));

        mutator.setSectionHelper(sectionHelper);
        mutator.setEdOrgHelper(edOrgHelper);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("client_id", ADMIN_APP_ID);
        body.put("is_admin", true);
        adminApp = repo.create("application", body, "application");

        body = new HashMap<String, Object>();
        body.put("client_id", "nonAdminAppId");
        body.put("is_admin", false);
        app = repo.create("application", body, "application");
    }

    @Test
    public void testV1Mutate() {
        // Testing that we don't crash the api if we do an api/v1/ request
        PathSegment v1 = Mockito.mock(PathSegment.class);
        when(v1.getPath()).thenReturn("v1");

        when(principal.getEntity()).thenReturn(staff);
        Assert.assertEquals("Bad endpoint of /v1 is redirected to v1/home safely", createMutatedContainer("/home", ""),
                mutator.mutate(Arrays.asList(v1), null, principal, "nonAdminAppId"));
        when(principal.getEntity()).thenReturn(teacher);
        Assert.assertEquals("Bad endpoint of /v1 is redirected to v1/home safely", createMutatedContainer("/home", ""),
                mutator.mutate(Arrays.asList(v1), null, principal, "nonAdminAppId"));
    }

    @Test
    public void testDeterministicRewrite() {
        // no user entity needed
        when(principal.getEntity()).thenReturn(null);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "teacher");
        Entity teacher = repo.create("teacher", body, "staff");
        PathSegment v1 = Mockito.mock(PathSegment.class);
        when(v1.getPath()).thenReturn("/staff");
        Assert.assertEquals("Endpoint should be rewritten to /teachers/id",
                createMutatedContainer("/teachers/" + teacher.getEntityId(), null),
                mutator.mutate(Arrays.asList(v1), "staffUniqueStateId=teacher", principal, null));

        body.put("staffUniqueStateId", "staff");
        teacher = repo.create("staff", body, "staff");
        v1 = Mockito.mock(PathSegment.class);
        when(v1.getPath()).thenReturn("/staff");
        Assert.assertEquals("Endpoint should be rewritten to /staff/id",
                createMutatedContainer("/staff/" + teacher.getEntityId(), null),
                mutator.mutate(Arrays.asList(v1), "staffUniqueStateId=staff", principal, null));
    }

    @Test
    // One part requests to the 'educationOrganizations' endpoint by a user with the APP_AUTHORIZE right
    // from an admin application should NOT be mutated
    public void testMutateSkipForAppAuthRightAdminApp() {
        SecurityUtil.setUserContext(SecurityUtil.UserContext.STAFF_CONTEXT);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", "staff");
        teacher = repo.create("staff", body, "staff");
        PathSegment v1 = Mockito.mock(PathSegment.class);
        when(v1.getPath()).thenReturn("/v1");
        PathSegment edorgs = Mockito.mock(PathSegment.class);
        when(edorgs.getPath()).thenReturn("/educationOrganizations");
        when(principal.getEntity()).thenReturn(staff);
        Map<String, Collection<GrantedAuthority>> edOrgRights = generateSimpleEdOrgRightsMap("theEdOrg", Right.APP_AUTHORIZE);
        when(principal.getEdOrgRights()).thenReturn(edOrgRights);
        MutatedContainer mutated = mutator.mutate(Arrays.asList(v1,edorgs), null, principal, ADMIN_APP_ID);
        System.out.println("The path is : " + mutated.getPath() + ", qparams : " + mutated.getQueryParameters());
        Assert.assertEquals("Endpoint should NOT have been rewritten to " + mutated.getPath(),
                createMutatedContainer(null, ""),
                mutated);
    }

    @Test
    public void testGetInferredUrisForTeacherContext() throws Exception {
        SecurityUtil.setUserContext(SecurityUtil.UserContext.TEACHER_CONTEXT);
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
    public void testGetInferredUrisForStaffContext() throws Exception {
        SecurityUtil.setUserContext(SecurityUtil.UserContext.STAFF_CONTEXT);
        HashMap<String, String> mutatedContentType = new HashMap<String, String>();
        mutatedContentType.put("Content-Type", "application/vnd.slc.search.full+json");

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.ASSESSMENTS + " is incorrect.",
                createMutatedContainer("/search/assessments", "", mutatedContentType),
                mutator.mutateBaseUri(VERSION, ResourceNames.ASSESSMENTS, "", staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.ATTENDANCES + " is incorrect.",
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

    @Test
    public void testGetInferredUrisForDualContext() throws Exception {
        SecurityUtil.setUserContext(SecurityUtil.UserContext.DUAL_CONTEXT);
        HashMap<String, String> mutatedContentType = new HashMap<String, String>();
        mutatedContentType.put("Content-Type", "application/vnd.slc.search.full+json");

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.ASSESSMENTS + " is incorrect.",
                createMutatedContainer("/search/assessments", "", mutatedContentType),
                mutator.mutateBaseUri(VERSION, ResourceNames.ASSESSMENTS, "", staff));

        Assert.assertEquals("inferred uri for staff resource: /" + ResourceNames.ATTENDANCES + " is incorrect.",
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

    private Map<String, Collection<GrantedAuthority>> generateSimpleEdOrgRightsMap(String edOrg, GrantedAuthority right) {
        Map<String, Collection<GrantedAuthority>> edOrgRights = new HashMap<String, Collection<GrantedAuthority>>();
        if (principal.getEdOrgRoles() != null) {
            Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            authorities.add(right);
            edOrgRights.put(edOrg, authorities);
        }

        return edOrgRights;
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
