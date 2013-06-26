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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;

/**
 * Unit test for TransitiveStudentToStaffValidator
 *
 * @author nbrown
 *
 */
public class StudentToStaffValidatorTest {

    private TransitiveStudentToStaffValidator underTest = new TransitiveStudentToStaffValidator();

    private Entity e = mock(Entity.class);

    @SuppressWarnings("unchecked")
    private PagingRepositoryDelegate<Entity> repo = mock(PagingRepositoryDelegate.class);

    private final List<String> staffIds = Arrays.asList("staff1", "staff2", "staff3", "staff4");

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        Map<String, List<Entity>> subDocs = new HashMap<String, List<Entity>>();
        subDocs.put("studentProgramAssociation",
                Arrays.asList(makeStudentProgram("program1"), makeStudentProgram("program2")));
        subDocs.put("studentCohortAssociation",
                Arrays.asList(makeStudentCohort("cohort1"), makeStudentCohort("cohort2")));
        when(e.getEmbeddedData()).thenReturn(subDocs);
        Map<String, List<Map<String, Object>>> denorms = new HashMap<String, List<Map<String, Object>>>();
        denorms.put("schools", Arrays.asList(makeStudentEdOrg("school1"), makeStudentEdOrg("school2")));
        denorms.put("section", Arrays.asList(makeStudentSection("section1"), makeStudentSection("section2")));
        when(e.getDenormalizedData()).thenReturn(denorms);
        underTest.setRepo(repo);
        underTest.setDateHelper(new DateHelper());
    }

    @Test
    public void testFilterConnectedViaProgram() {
        Iterator<Entity> staffProgramList = Arrays.asList(makeStaffProgram("staff1", "program1"),
                makeStaffProgram("staff1", "program2"), makeStaffProgram("staff2", "program1"),
                makeStaffProgram("staff3", "program3")).iterator();
        when(repo.findEach(eq("staffProgramAssociation"), any(Query.class))).thenReturn(staffProgramList);
        assertEquals(new HashSet<String>(Arrays.asList("staff1", "staff2", "staff3")),
                underTest.filterConnectedViaProgram(new HashSet<String>(staffIds), e));
    }

    @Test
    public void testFilterConnectedViaCohort() {
        Iterator<Entity> staffCohortList = Arrays.asList(makeStaffCohort("staff1", "cohort1"),
                makeStaffCohort("staff1", "cohort2"), makeStaffCohort("staff2", "cohort1"),
                makeStaffCohort("staff3", "cohort3")).iterator();
        when(repo.findEach(eq("staffCohortAssociation"), any(Query.class))).thenReturn(staffCohortList);
        assertEquals(new HashSet<String>(Arrays.asList("staff1", "staff2", "staff3")),
                underTest.filterConnectedViaCohort(new HashSet<String>(staffIds), e));
    }

    @Test
    public void testFilterConnectedViaEdOrg() {
        Iterator<Entity> staffEdOrgList = Arrays.asList(makeStaffEdOrg("staff1", "school1"),
                makeStaffEdOrg("staff1", "school2"), makeStaffEdOrg("staff2", "school1"),
                makeStaffEdOrg("staff3", "school3")).iterator();
        when(repo.findEach(eq("staffEducationOrganizationAssociation"), any(Query.class))).thenReturn(staffEdOrgList);
        assertEquals(new HashSet<String>(Arrays.asList("staff1", "staff2", "staff3")),
                underTest.filterConnectedViaEdOrg(new HashSet<String>(staffIds), e));
    }

    @Test
    public void testFilterConnectedViaSection() {
        Entity section1 = mock(Entity.class);
        Map<String, List<Entity>> section1SubDocs = new HashMap<String, List<Entity>>();
        section1SubDocs.put("teacherSectionAssociation",
                Arrays.asList(makeTeacherSection("staff1", "section1"), makeTeacherSection("staff2", "section1")));
        when(section1.getEmbeddedData()).thenReturn(section1SubDocs);
        Entity section2 = mock(Entity.class);
        Map<String, List<Entity>> section2SubDocs = new HashMap<String, List<Entity>>();
        section2SubDocs.put("teacherSectionAssociation", Arrays.asList(makeTeacherSection("staff1", "section2")));
        when(section2.getEmbeddedData()).thenReturn(section2SubDocs);
        Entity section3 = mock(Entity.class);
        Map<String, List<Entity>> section3SubDocs = new HashMap<String, List<Entity>>();
        section3SubDocs.put("teacherSectionAssociation", Arrays.asList(makeTeacherSection("staff3", "section3")));
        when(section3.getEmbeddedData()).thenReturn(section3SubDocs);
        when(repo.findEach(eq("section"), any(Query.class))).thenReturn(
                Arrays.asList(section1, section2, section3).iterator());
        assertEquals(new HashSet<String>(Arrays.asList("staff1", "staff2", "staff3")),
                underTest.filterConnectedViaSection(new HashSet<String>(staffIds), e));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLonelyStudent() {
        Entity lonely = mock(Entity.class);
        when(lonely.getDenormalizedData()).thenReturn(Collections.EMPTY_MAP);
        when(lonely.getEmbeddedData()).thenReturn(Collections.EMPTY_MAP);
        assertEquals(Collections.emptySet(), underTest.filterConnectedViaEdOrg(new HashSet<String>(staffIds), lonely));
    }

    @Test
    public void testOldStudent() {
        Iterator<Entity> staffProgramList = Arrays.asList(makeStaffProgram("staff1", "program1"),
                makeStaffProgram("staff1", "program2"), makeStaffProgram("staff2", "program1"),
                makeStaffProgram("staff3", "program3")).iterator();
        when(repo.findEach(eq("staffProgramAssociation"), any(Query.class))).thenReturn(staffProgramList);
        Iterator<Entity> staffCohortList = Arrays.asList(makeStaffCohort("staff1", "cohort1"),
                makeStaffCohort("staff1", "cohort2"), makeStaffCohort("staff2", "cohort1"),
                makeStaffCohort("staff3", "cohort3")).iterator();
        when(repo.findEach(eq("staffCohortAssociation"), any(Query.class))).thenReturn(staffCohortList);
        Iterator<Entity> staffEdOrgList = Arrays.asList(makeStaffEdOrg("staff1", "school1"),
                makeStaffEdOrg("staff1", "school2"), makeStaffEdOrg("staff2", "school1"),
                makeStaffEdOrg("staff3", "school3")).iterator();
        when(repo.findEach(eq("staffEducationOrganizationAssociation"), any(Query.class))).thenReturn(staffEdOrgList);
        for (List<Entity> subDocs : e.getEmbeddedData().values()) {
            for (Entity subDoc : subDocs) {
                subDoc.getBody().put("endDate", "1999-12-31");
            }
        }
        for (List<Map<String, Object>> denorms : e.getDenormalizedData().values()) {
            for (Map<String, Object> denorm : denorms) {
                denorm.put("endDate", "1999-12-31");
            }
        }
        assertEquals(Collections.emptySet(), underTest.filterConnectedViaEdOrg(new HashSet<String>(staffIds), e));
    }

    private Entity makeStaffProgram(String staffId, String programId) {
        Entity spa = mock(Entity.class);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("programId", programId);
        body.put("staffId", staffId);
        when(spa.getBody()).thenReturn(body);
        return spa;

    }

    private Entity makeStudentProgram(String id) {
        Entity spa = mock(Entity.class);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("programId", id);
        when(spa.getBody()).thenReturn(body);
        return spa;
    }

    private Entity makeStaffCohort(String staffId, String programId) {
        Entity sca = mock(Entity.class);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("cohortId", programId);
        body.put("staffId", staffId);
        when(sca.getBody()).thenReturn(body);
        return sca;

    }

    private Entity makeStudentCohort(String id) {
        Entity sca = mock(Entity.class);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("cohortId", id);
        when(sca.getBody()).thenReturn(body);
        return sca;
    }

    private Entity makeStaffEdOrg(String staffId, String edOrgId) {
        Entity sea = mock(Entity.class);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", edOrgId);
        body.put("staffReference", staffId);
        when(sea.getBody()).thenReturn(body);
        return sea;

    }

    private Map<String, Object> makeStudentEdOrg(String id) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("_id", id);
        return body;
    }

    private Entity makeTeacherSection(String staffId, String sectionId) {
        Entity sea = mock(Entity.class);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("sectionId", sectionId);
        body.put("teacherId", staffId);
        when(sea.getBody()).thenReturn(body);
        return sea;

    }

    private Map<String, Object> makeStudentSection(String id) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("_id", id);
        return body;
    }

}
