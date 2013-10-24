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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.*;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import org.slc.sli.bulk.extract.lea.ExtractorHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.utils.EdOrgHierarchyHelper;

/**
 * Unit test for the student context resolver
 * 
 * @author nbrown
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class StudentContextResolverTest {
    @InjectMocks
    private final StudentContextResolver underTest = new StudentContextResolver();
    @Mock
    private Repository<Entity> repo;
    @Mock
    private EducationOrganizationContextResolver edOrgResolver;
    
    private DateHelper helper = new DateHelper();

    private ExtractorHelper extractorHelper = Mockito.mock(ExtractorHelper.class);

    private final static String NONCURSCHOOL = "nonCurrentSchool";
    private final static String CURRENTSCHOOL = "currentSchool";
    private final static String FUTURESCHOOL = "futureSchool";
    private final static String UNBOUNDSCHOOL = "unboundSchool";

    @Before
    public void setup() {
        underTest.getCache().clear();
        underTest.setDateHelper(helper);

        underTest.setExtractorHelper(extractorHelper);
    }
    
    @Test
    public void testFindGoverningLEA() {
        Entity student = buildTestStudent();
        assertEquals(new HashSet<String>(Arrays.asList("edOrg1", "edOrg2", "edOrg3")),
                underTest.findGoverningEdOrgs(student));

    }
    
    @Test
    public void testGetLEAsForStudentId() {
        Entity testStudent = buildTestStudent();
        when(repo.findOne("student", buildQuery("42"))).thenReturn(testStudent);
        assertEquals(new HashSet<String>(Arrays.asList("edOrg1", "edOrg2", "edOrg3")),
                underTest.findGoverningEdOrgs("42"));

    }

    @Test
    public void testResolveDatedEntity() {
        Map<String, DateTime> edorgDates = new HashMap<String, DateTime>();

        Entity testStudent = buildTestStudent("42", edorgDates);
        Entity spa =buildSPA("spa1", "2012-10-01", "2013-02-01");
        Mockito.when(extractorHelper.fetchAllEdOrgsForStudent((Entity) Matchers.any())).thenReturn(edorgDates);

        Entity nonCurrentEdorg = buildEdorg(NONCURSCHOOL, EntityNames.SCHOOL);
        Entity current = buildEdorg(CURRENTSCHOOL, "State Education Agency");

        List<Entity> edorgs = new ArrayList<Entity>(Arrays.asList(nonCurrentEdorg, current));

        when(repo.findAll(Matchers.eq(EntityNames.EDUCATION_ORGANIZATION), (NeutralQuery)Matchers.any())).thenReturn(edorgs);

        EdOrgHierarchyHelper edOrgHierarchyHelper = mock(EdOrgHierarchyHelper.class);
        when(edOrgHierarchyHelper.isSEA(current)).thenReturn(true);
        underTest.setEdOrgHierarchyHelper(edOrgHierarchyHelper);

        StudentContextResolver rs = Mockito.spy(underTest);
        Mockito.doReturn(true).when(rs).shouldExtract(Mockito.eq(spa), Mockito.any(DateTime.class));

        Set<String> res = rs.findGoverningEdOrgs(testStudent, spa);

        Set<String> expected = new HashSet<String>(Arrays.asList(NONCURSCHOOL));
        assertEquals(res, expected);
    }

    @Test
    public void testResolveNonDatedEntity() {
        Map<String, DateTime> edorgDates = new HashMap<String, DateTime>();

        Entity testStudent = buildTestStudent("42", edorgDates);
        Mockito.when(extractorHelper.fetchAllEdOrgsForStudent((Entity) Matchers.any())).thenReturn(edorgDates);

        Entity nonCurrentEdorg = buildEdorg(NONCURSCHOOL, EntityNames.SCHOOL);
        Entity current = buildEdorg(CURRENTSCHOOL, EntityNames.SCHOOL);

        List<Entity> edorgs = new ArrayList<Entity>(Arrays.asList(nonCurrentEdorg, current));

        when(repo.findAll(Matchers.eq(EntityNames.EDUCATION_ORGANIZATION), (NeutralQuery)Matchers.any())).thenReturn(edorgs);

        EdOrgHierarchyHelper edOrgHierarchyHelper = mock(EdOrgHierarchyHelper.class);
        when(edOrgHierarchyHelper.isSEA(Matchers.any(Entity.class))).thenReturn(false);
        underTest.setEdOrgHierarchyHelper(edOrgHierarchyHelper);

        Set<String> res = underTest.findGoverningEdOrgs(testStudent, testStudent);

        Set<String> expected = new HashSet<String>(Arrays.asList(NONCURSCHOOL, CURRENTSCHOOL));
        assertEquals(res, expected);
    }

    private Entity buildEdorg(String edorgId, String type) {
        Entity edorg = mock(Entity.class);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ParameterConstants.ORGANIZATION_CATEGORIES, type);

        Mockito.when(edorg.getEntityId()).thenReturn(edorgId);
        Mockito.when(edorg.getBody()).thenReturn(body);

        return edorg;
    }

    private NeutralQuery buildEdorgQuery(List<String> edorgIds) {
        return new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.OPERATOR_EQUAL, edorgIds));
    }
    
    @SuppressWarnings("unchecked")
    private Entity buildTestStudent() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("studentUniqueStateId", "42");
        DateTime today = DateTime.now();
        DateTimeFormatter format = ISODateTimeFormat.date();
        Map<String, Object> oldSchool = new HashMap<String, Object>();
        oldSchool.put("_id", "oldSchool");
        oldSchool.put("entryDate", today.minusMonths(15).toString(format));
        oldSchool.put("exitWithdrawDate", today.minusMonths(3).toString(format));
        when(edOrgResolver.findGoverningEdOrgs("oldSchool")).thenReturn(new HashSet<String>(Arrays.asList("badEdOrg")));
        Map<String, Object> currentSchool = new HashMap<String, Object>();
        currentSchool.put("_id", "currentSchool");
        currentSchool.put("entryDate", today.minusMonths(3).toString(format));
        currentSchool.put("exitWithdrawDate", today.plusMonths(9).toString(format));
        when(edOrgResolver.findGoverningEdOrgs("currentSchool")).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        Map<String, Object> futureSchool = new HashMap<String, Object>();
        futureSchool.put("_id", "futureSchool");
        futureSchool.put("entryDate", today.plusMonths(9).toString(format));
        futureSchool.put("exitWithdrawDate", today.plusMonths(21).toString(format));
        when(edOrgResolver.findGoverningEdOrgs("futureSchool")).thenReturn(new HashSet<String>(Arrays.asList("edOrg2")));
        Map<String, Object> unboundedSchool = new HashMap<String, Object>();
        unboundedSchool.put("_id", "unboundedSchool");
        unboundedSchool.put("entryDate", today.minusMonths(15).toString(format));
        when(edOrgResolver.findGoverningEdOrgs("unboundedSchool")).thenReturn(new HashSet<String>(Arrays.asList("edOrg3")));
        Map<String, List<Map<String, Object>>> denormalized = new HashMap<String, List<Map<String, Object>>>();
        denormalized.put("schools", Arrays.asList(oldSchool, currentSchool, futureSchool, unboundedSchool));
        Entity testStudent = mock(Entity.class);
        when(testStudent.getEntityId()).thenReturn("testStudent");
        when(testStudent.getBody()).thenReturn(body);
        when(testStudent.getDenormalizedData()).thenReturn(denormalized);
        return testStudent;
    }

    private Entity buildTestStudent(String studentId, Map<String, DateTime> edorgDates) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("studentUniqueStateId", studentId);
        DateTime today = DateTime.now();
        DateTimeFormatter format = ISODateTimeFormat.date();
        Map<String, Object> oldSchool = new HashMap<String, Object>();
        oldSchool.put("_id", NONCURSCHOOL);
        oldSchool.put("entryDate", today.minusMonths(15).toString(format));
        oldSchool.put("exitWithdrawDate", today.minusMonths(3).toString(format));
        edorgDates.put(NONCURSCHOOL, today.minusMonths(3));
        when(edOrgResolver.findGoverningEdOrgs(NONCURSCHOOL)).thenReturn(new HashSet<String>(Arrays.asList(NONCURSCHOOL)));

        Map<String, Object> currentSchool = new HashMap<String, Object>();
        currentSchool.put("_id", CURRENTSCHOOL);
        currentSchool.put("entryDate", today.minusMonths(15).toString(format));
        currentSchool.put("exitWithdrawDate", today.plusMonths(9).toString(format));
        edorgDates.put(CURRENTSCHOOL, today.plusMonths(9));
        when(edOrgResolver.findGoverningEdOrgs(CURRENTSCHOOL)).thenReturn(new HashSet<String>(Arrays.asList(CURRENTSCHOOL)));

        Map<String, Object> futureSchool = new HashMap<String, Object>();
        futureSchool.put("_id", FUTURESCHOOL);
        futureSchool.put("entryDate", today.plusMonths(9).toString(format));
        futureSchool.put("exitWithdrawDate", today.plusMonths(21).toString(format));
        edorgDates.put(FUTURESCHOOL, today.plusMonths(21));
        when(edOrgResolver.findGoverningEdOrgs(FUTURESCHOOL)).thenReturn(new HashSet<String>(Arrays.asList(FUTURESCHOOL)));

        Map<String, Object> unboundedSchool = new HashMap<String, Object>();
        unboundedSchool.put("_id", UNBOUNDSCHOOL);
        unboundedSchool.put("entryDate", today.minusMonths(15).toString(format));
        edorgDates.put(UNBOUNDSCHOOL, null);
        when(edOrgResolver.findGoverningEdOrgs(UNBOUNDSCHOOL)).thenReturn(new HashSet<String>(Arrays.asList(UNBOUNDSCHOOL)));

        Map<String, List<Map<String, Object>>> denormalized = new HashMap<String, List<Map<String, Object>>>();
        denormalized.put("schools", Arrays.asList(oldSchool, currentSchool, futureSchool, unboundedSchool));
        Entity testStudent = mock(Entity.class);
        when(testStudent.getEntityId()).thenReturn("testStudent");
        when(testStudent.getBody()).thenReturn(body);
        when(testStudent.getDenormalizedData()).thenReturn(denormalized);
        when(testStudent.getType()).thenReturn(EntityNames.STUDENT);
        return testStudent;
    }

    private NeutralQuery buildQuery(String id) {
        return new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.OPERATOR_EQUAL, id))
                .setEmbeddedFieldString("schools");
    }

    Entity buildSPA(String spaId, String beginDate, String endDate) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ParameterConstants.ID, spaId);
        body.put(ParameterConstants.BEGIN_DATE, beginDate);

        if(endDate != null) {
            body.put(ParameterConstants.END_DATE, endDate);
        }

        Entity spa = mock(Entity.class);
        when(spa.getEntityId()).thenReturn(spaId);
        when(spa.getType()).thenReturn(EntityNames.STUDENT_PROGRAM_ASSOCIATION);
        when(spa.getBody()).thenReturn(body);
        return spa;
    }
    
    @Test
    public void testCache() {
        Entity testEntity = buildTestStudent();
        Set<String> fromEntity1 = underTest.findGoverningEdOrgs(testEntity);
        Set<String> fromId = underTest.findGoverningEdOrgs("testStudent");
        Set<String> fromEntity2 = underTest.findGoverningEdOrgs(testEntity);
        assertEquals(fromEntity1, fromId);
        assertEquals(fromEntity2, fromId);
    }
}
