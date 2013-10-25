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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.bulk.extract.lea.ExtractorHelper;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.utils.EdOrgHierarchyHelper;

public class StaffTeacherContextResolverTest {
    
    @InjectMocks
    StaffTeacherContextResolver resolver = new StaffTeacherContextResolver();
    
    private DateHelper dateHelper = new DateHelper();
    
    @Mock
    private Repository<Entity> repo;
    
    @Mock
    private EducationOrganizationContextResolver edOrgResolver;
    
    @Mock
    private EdOrgHierarchyHelper edOrgHelper;

    @Mock
    private EdOrgExtractHelper edOrgExtractHelper;

    private ExtractorHelper extractorHelper = Mockito.mock(ExtractorHelper.class);
    

    @Before
    public void setUp() throws Exception {
        dateHelper = Mockito.mock(DateHelper.class);
        edOrgResolver = Mockito.mock(EducationOrganizationContextResolver.class);
        MockitoAnnotations.initMocks(this);
        resolver.setDateHelper(dateHelper);
        resolver.setExtractorHelper(extractorHelper);
    }

    @Test
    public void expiredStaffDoNotShowup() {
        Entity staff = buildStaffEntity();
        NeutralQuery q = resolver.buildStaffEdorgQuery(staff.getEntityId());
        when(repo.findAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, q)).thenReturn(buildExpiredAssociations());
        assertTrue(resolver.findGoverningEdOrgs(staff).size() == 0);
    }
    
    @Test
    public void staffWithAssociationsToTwoLEAsBelongsToTwoLEAs() {
        Entity staff = buildStaffEntity();
        NeutralQuery q = resolver.buildStaffEdorgQuery(staff.getEntityId());
        when(repo.findAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, q)).thenReturn(buildCurrentAssociations());
        String tomorrow = new DateTime().plusDays(1).toString("yyyy-MM-dd");
        String dayAfterTomorrow = new DateTime().plusDays(2).toString("yyyy-MM-dd");
        Set<String> lea1 = new HashSet<String>(Arrays.asList("lea1"));
        Set<String> lea2 = new HashSet<String>(Arrays.asList("lea2"));
        when(edOrgResolver.findGoverningEdOrgs("edorg" + tomorrow)).thenReturn(lea1);
        when(edOrgResolver.findGoverningEdOrgs("edorg" + dayAfterTomorrow)).thenReturn(lea2);
        System.out.println(resolver.findGoverningEdOrgs(staff));
        assertTrue(resolver.findGoverningEdOrgs(staff).size() == 2);
        Set<String> all = new HashSet<String>(Arrays.asList("lea1", "lea2"));
        assertEquals(all, resolver.findGoverningEdOrgs(staff));
    }

    @Test
    public void testMulipleSEOA() {
        Entity staff = buildStaffEntity();
        Entity entityToExtract = Mockito.mock(Entity.class);
        Mockito.when(entityToExtract.getType()).thenReturn(EntityNames.STAFF_COHORT_ASSOCIATION);

        DateTime seoa1Date = new DateTime();
        Entity seoa1 = buildSeoa(staff.getEntityId(), "edOrg1", seoa1Date, seoa1Date.minusMonths(1));
        DateTime seoa2Date = new DateTime().minusYears(1);
        Entity seoa2 =buildSeoa(staff.getEntityId(), "edOrg3", seoa2Date, seoa2Date.minusMonths(5));

        NeutralQuery q = resolver.buildStaffEdorgQuery(staff.getEntityId());
        when(repo.findAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, q)).thenReturn(Arrays.asList(seoa1, seoa2));

        StaffTeacherContextResolver spyResolver = Mockito.spy(resolver);

        Mockito.doReturn(true).when(spyResolver).shouldExtract(entityToExtract, seoa1Date);
        Mockito.doReturn(false).when(spyResolver).shouldExtract(entityToExtract, seoa2Date);

        Map<String, DateTime> edOrgDate = new HashMap<String, DateTime>();
        edOrgDate.put("edOrg1", seoa1Date);
        edOrgDate.put("edOrg2", seoa1Date);

        Mockito.when(extractorHelper.updateEdorgToDateMap(seoa1.getBody(), new HashMap<String, DateTime>(), ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE,
                ParameterConstants.BEGIN_DATE, ParameterConstants.END_DATE)).thenReturn(edOrgDate);
        Mockito.when(extractorHelper.updateEdorgToDateMap(seoa2.getBody(), new HashMap<String, DateTime>(), ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE,
                ParameterConstants.BEGIN_DATE, ParameterConstants.END_DATE)).thenReturn(new HashMap<String, DateTime>());

        assertEquals(new HashSet<String>(Arrays.asList("edOrg1", "edOrg2")), spyResolver.resolve(staff, entityToExtract));
    }

    @Test
    public void testFutureSEOA() {
        Entity staff = buildStaffEntity();
        Entity entityToExtract = Mockito.mock(Entity.class);
        Mockito.when(entityToExtract.getType()).thenReturn(EntityNames.STAFF_COHORT_ASSOCIATION);

        DateTime seoaDate = new DateTime().plusYears(1);
        Entity seoa = buildSeoa(staff.getEntityId(), "edOrg1", seoaDate, seoaDate.minusMonths(1));

        NeutralQuery q = resolver.buildStaffEdorgQuery(staff.getEntityId());
        when(repo.findAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, q)).thenReturn(Arrays.asList(seoa));

        StaffTeacherContextResolver spyResolver = Mockito.spy(resolver);

        Mockito.doReturn(false).when(spyResolver).shouldExtract(entityToExtract, seoaDate);
        Mockito.when(extractorHelper.updateEdorgToDateMap(seoa.getBody(), new HashMap<String, DateTime>(), ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE,
                ParameterConstants.BEGIN_DATE, ParameterConstants.END_DATE)).thenReturn(new HashMap<String, DateTime>());
        assertEquals(0, spyResolver.resolve(staff, entityToExtract).size());
    }

    @Test
    public void testNonDatedEntities() {
        Entity staff = buildStaffEntity();
        Entity entityToExtract = Mockito.mock(Entity.class);
        Mockito.when(entityToExtract.getType()).thenReturn(EntityNames.STAFF);

        DateTime seoaDate = new DateTime().plusYears(1);
        Entity seoa = buildSeoa(staff.getEntityId(), "edOrg1", seoaDate, seoaDate.minusMonths(1));

        NeutralQuery q = resolver.buildStaffEdorgQuery(staff.getEntityId());
        when(repo.findAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, q)).thenReturn(Arrays.asList(seoa));

        StaffTeacherContextResolver spyResolver = Mockito.spy(resolver);

        Mockito.doReturn(false).when(spyResolver).shouldExtract(entityToExtract, seoaDate);
        Mockito.when(extractorHelper.updateEdorgToDateMap(seoa.getBody(), new HashMap<String, DateTime>(), ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE,
                ParameterConstants.BEGIN_DATE, ParameterConstants.END_DATE)).thenReturn(new HashMap<String, DateTime>());
        assertEquals(0, spyResolver.resolve(staff, entityToExtract).size());
    }

    private Iterable<Entity> buildCurrentAssociations() {
        String tomorrow = new DateTime().plusDays(1).toString("yyyy-MM-dd");
        String dayAfterTomorrow = new DateTime().plusDays(2).toString("yyyy-MM-dd");
        return Arrays.asList(buildAssociations(tomorrow), buildAssociations(dayAfterTomorrow));
    }

    private Iterable<Entity> buildExpiredAssociations() {
        String yesterday = new DateTime().minusDays(1).toString("yyyy-MM-dd");
        return Arrays.asList(buildAssociations(yesterday));
    }

    private Entity buildAssociations(String endDate) {
        Map<String, Object> body = new HashMap<String, Object>();
        if (endDate != null) {
            body.put("endDate", endDate);
            body.put("educationOrganizationReference", "edorg" + endDate);
        }
        
        return new MongoEntity(EntityNames.STAFF_ED_ORG_ASSOCIATION, "staffEdorgAssociationId", body, new HashMap<String, Object>());
    }

    private Entity buildSeoa(String staffId, String edOrgId, DateTime entryDate, DateTime endDate) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ParameterConstants.STAFF_ID, staffId);
        body.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, edOrgId);
        body.put(ParameterConstants.ENTRY_DATE, entryDate);
        body.put(ParameterConstants.END_DATE, endDate);
        return new MongoEntity(EntityNames.STAFF_ED_ORG_ASSOCIATION, "staffEdorgAssociationId", body, new HashMap<String, Object>());
    }

    private Entity buildStaffEntity() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("loginId", "myId");
        return new MongoEntity("staff", "staffId", body, new HashMap<String, Object>());
    }

    private Map<String, List<String>> buildEdOrgLineage() {
        Map<String, List<String>> lineage = new HashMap<String, List<String>>();
        lineage.put("edOrg1", Arrays.asList("edOrg1", "edOrg2"));
        return lineage;
    }
}
