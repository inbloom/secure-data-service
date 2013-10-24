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
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
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
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.utils.EdOrgHierarchyHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private ExtractorHelper extractorHelper = Mockito.mock(ExtractorHelper.class);

    private final static String NONCURSCHOOL = "nonCurrentSchool";
    private final static String CURRENTSCHOOL = "currentSchool";
    private final static String FUTURESCHOOL = "futureSchool";
    private final static String UNBOUNDSCHOOL = "unboundSchool";

    DateTime today = DateTime.now();
    DateTimeFormatter format = ISODateTimeFormat.date();

    @Before
    public void setup() {
        underTest.getCache().clear();

        underTest.setExtractorHelper(extractorHelper);
    }

    @Test
    public void testResolveDatedEntity() {
        Map<String, DateTime> edorgDates = new HashMap<String, DateTime>();


        Entity testStudent = buildTestStudent("42", edorgDates);
        Entity spa =buildSPA("spa1", today.minusMonths(2).toString(format), today.plusMonths(1).toString());
        Mockito.when(extractorHelper.fetchAllEdOrgsForStudent((Entity) Matchers.any())).thenReturn(edorgDates);

        Entity current = buildEdorg(CURRENTSCHOOL, "State Education Agency");

        when(repo.findOne(Matchers.eq(EntityNames.EDUCATION_ORGANIZATION), (NeutralQuery)Matchers.any())).thenReturn(current);

        final DateTime nonCurrentDate = edorgDates.get(NONCURSCHOOL);
        final DateTime futureDate = edorgDates.get(FUTURESCHOOL);

        EdOrgHierarchyHelper edOrgHierarchyHelper = mock(EdOrgHierarchyHelper.class);
        when(edOrgHierarchyHelper.getSEA()).thenReturn(current);
        underTest.setEdOrgHierarchyHelper(edOrgHierarchyHelper);

        StudentContextResolver rs = Mockito.spy(underTest);
        Mockito.doReturn(false).when(rs).shouldExtract(Matchers.eq(spa),argThat(buildMatchers(nonCurrentDate)));
        Mockito.doReturn(true).when(rs).shouldExtract(Matchers.eq(spa), argThat(buildMatchers(futureDate)));

        Set<String> res = rs.resolve(testStudent, spa);

        Set<String> expected = new HashSet<String>(Arrays.asList(UNBOUNDSCHOOL, FUTURESCHOOL));
        assertEquals(res, expected);
    }

    @Test
    public void testResolveNonDatedEntity() {
        Map<String, DateTime> edorgDates = new HashMap<String, DateTime>();

        Entity testStudent = buildTestStudent("42", edorgDates);
        Mockito.when(extractorHelper.fetchAllEdOrgsForStudent((Entity) Matchers.any())).thenReturn(edorgDates);


        when(repo.findOne(Matchers.eq(EntityNames.EDUCATION_ORGANIZATION), (NeutralQuery)Matchers.any())).thenReturn(null);
        EdOrgHierarchyHelper edOrgHierarchyHelper = mock(EdOrgHierarchyHelper.class);
        when(edOrgHierarchyHelper.getSEA()).thenReturn(null);
        underTest.setEdOrgHierarchyHelper(edOrgHierarchyHelper);

        Set<String> res = underTest.resolve(testStudent, testStudent);

        Set<String> expected = new HashSet<String>(Arrays.asList(CURRENTSCHOOL, NONCURSCHOOL, FUTURESCHOOL, UNBOUNDSCHOOL));
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

    private BaseMatcher<DateTime> buildMatchers(final DateTime dateTime) {
        return new BaseMatcher<DateTime>() {

            @Override
            public boolean matches(Object arg0) {
                DateTime target = (DateTime) arg0;
                return target == null || target.equals(dateTime);
            }

            @Override
            public void describeTo(Description arg0) {
            }
        };
    }

    private Entity buildTestStudent(String studentId, Map<String, DateTime> edorgDates) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("studentUniqueStateId", studentId);

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

}
