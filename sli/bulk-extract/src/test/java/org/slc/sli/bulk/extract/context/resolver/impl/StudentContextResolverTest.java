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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.common.inject.matcher.Matchers;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import org.slc.sli.bulk.extract.lea.ExtractorHelper;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

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

    @Before
    public void setup() {
        underTest.getCache().clear();
        underTest.setDateHelper(helper);

        underTest.setExtractorHelper(extractorHelper);
    }
    
    @Test
    @Ignore
    public void testFindGoverningLEA() {
        //Map<String, DateTime> edorgDates
        //Mockito.when(extractorHelper.fetchAllEdOrgsForStudent(Matchers.any(Entity.class))).thenReturn();

        assertEquals(new HashSet<String>(Arrays.asList("edOrg1", "edOrg2", "edOrg3")),
                underTest.findGoverningEdOrgs(buildTestStudent()));
    }
    
    @Test
    @Ignore
    public void testGetLEAsForStudentId() {
        Entity testStudent = buildTestStudent();
        when(repo.findOne("student", buildQuery("42"))).thenReturn(testStudent);
        assertEquals(new HashSet<String>(Arrays.asList("edOrg1", "edOrg2", "edOrg3")),
                underTest.findGoverningEdOrgs("42"));
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
    
    private NeutralQuery buildQuery(String id) {
        return new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.OPERATOR_EQUAL, id))
                .setEmbeddedFieldString("schools");
    }
    
    @Test
    @Ignore
    public void testCache() {
        Entity testEntity = buildTestStudent();
        Set<String> fromEntity1 = underTest.findGoverningEdOrgs(testEntity);
        Set<String> fromId = underTest.findGoverningEdOrgs("testStudent");
        Set<String> fromEntity2 = underTest.findGoverningEdOrgs(testEntity);
        assertEquals(fromEntity1, fromId);
        assertEquals(fromEntity2, fromId);
    }
}
