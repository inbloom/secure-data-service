/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.api.security.context;

import static org.mockito.Matchers.argThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * @author npandey ablum
 */
@SuppressWarnings("ALL")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@DirtiesContext
public class EdOrgOwnershipArbiterTest {

    @Autowired
    @InjectMocks
    private EdOrgOwnershipArbiter arbiter;

    @Mock
    private PagingRepositoryDelegate<Entity> repo;

    @Mock
    private EdOrgHelper helper;

    @Autowired
    private EntityDefinitionStore store;

    @Autowired
    SecurityContextInjector securityContextInjector;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void clean() {
        arbiter = null;
    }

    @Test
    public void testEdOrgReferenceEntities() {
        securityContextInjector.setEducatorContext();

     Entity edorg = createEntity(EntityNames.SCHOOL, "edorg1", new HashMap<String, Object>());
     Mockito.when(repo.findAll(Mockito.eq(EntityNames.EDUCATION_ORGANIZATION), argThat(new BaseMatcher<NeutralQuery>() {

         @Override
         public boolean matches(Object item) {
             NeutralQuery matching =  new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.OPERATOR_EQUAL, "edorg1"));
             NeutralQuery other = (NeutralQuery) item;
             return matching.getCriteria().equals(other.getCriteria());
         }

         @Override
         public void describeTo(Description description) {
             // TODO Auto-generated method stub

         }
     }))).thenReturn(Arrays.asList(edorg));
     Map<String, Object> attendanceBody = new HashMap<String, Object>();
     attendanceBody.put(ParameterConstants.SCHOOL_ID, "edorg1");
     Entity attendance = createEntity(EntityNames.ATTENDANCE, "attendance1", attendanceBody);

     Set<String> edorgIds = arbiter.determineEdorgs(Arrays.asList(attendance), EntityNames.ATTENDANCE);

     Assert.assertEquals(1, edorgIds.size());
     Assert.assertTrue(edorgIds.contains("edorg1"));

    }

    @Test
    public void testEdOrgFromAssociation() {
        securityContextInjector.setEducatorContext();

        Entity edorg1 = createEntity(EntityNames.SCHOOL, "edorg1", new HashMap<String, Object>());
        Entity edorg2 = createEntity(EntityNames.SCHOOL, "edorg2", new HashMap<String, Object>());
        Entity edorg3 = createEntity(EntityNames.SCHOOL, "edorg3", new HashMap<String, Object>());
        Mockito.when(repo.findAll(Mockito.eq(EntityNames.EDUCATION_ORGANIZATION), argThat(new BaseMatcher<NeutralQuery>() {

            @SuppressWarnings("unchecked")
            @Override
            public boolean matches(Object item) {
                NeutralQuery matching1 = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.OPERATOR_EQUAL, "edorg1"));
                NeutralQuery matching2 = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.OPERATOR_EQUAL, "edorg2"));
                NeutralQuery matching3 = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.OPERATOR_EQUAL, "edorg3"));

                NeutralQuery other = (NeutralQuery) item;
                return matching1.getCriteria().equals(other.getCriteria()) || matching2.getCriteria().equals(other.getCriteria()) || matching3.getCriteria().equals(other.getCriteria());
            }

            @Override
            public void describeTo(Description description) {
                // TODO Auto-generated method stub

            }
        }))).thenReturn(Arrays.asList(edorg1), Arrays.asList(edorg2), Arrays.asList(edorg3));

        Entity student1 = createEntity(EntityNames.STUDENT, "student1", new HashMap<String, Object>());

        Map<String, Object> ssa1Body = new HashMap<String, Object>();
        ssa1Body.put(ParameterConstants.SCHOOL_ID, "edorg1");
        ssa1Body.put(ParameterConstants.STUDENT_ID, "student1");
        Entity ssa1 = createEntity(EntityNames.STUDENT_SCHOOL_ASSOCIATION, "ssa1", ssa1Body);

        Map<String, Object> ssa2Body = new HashMap<String, Object>();
        ssa2Body.put(ParameterConstants.SCHOOL_ID, "edorg2");
        ssa2Body.put(ParameterConstants.STUDENT_ID, "student1");
        Entity ssa2 = createEntity(EntityNames.STUDENT_SCHOOL_ASSOCIATION, "ssa2", ssa2Body);

        Map<String, Object> ssa3Body = new HashMap<String, Object>();
        ssa3Body.put(ParameterConstants.SCHOOL_ID, "edorg3");
        ssa3Body.put(ParameterConstants.STUDENT_ID, "student1");
        Entity ssa3 = createEntity(EntityNames.STUDENT_SCHOOL_ASSOCIATION, "ssa3", ssa3Body);

        Mockito.when(repo.findAll(Mockito.eq(EntityNames.STUDENT_SCHOOL_ASSOCIATION), argThat(new BaseMatcher<NeutralQuery>() {

            @Override
            public boolean matches(Object item) {
                NeutralQuery matching = new NeutralQuery(new NeutralCriteria(ParameterConstants.STUDENT_ID, NeutralCriteria.OPERATOR_EQUAL, "student1"));
                NeutralQuery other = (NeutralQuery) item;
                return matching.getCriteria().equals(other.getCriteria());
            }

            @Override
            public void describeTo(Description description) {
                // TODO Auto-generated method stub

            }
        }))).thenReturn(Arrays.asList(ssa1, ssa2, ssa3));

        Set<String> edorgIds = arbiter.determineEdorgs(Arrays.asList(student1), EntityNames.STUDENT);

        Assert.assertEquals(3, edorgIds.size());
        Assert.assertTrue(edorgIds.contains("edorg1"));
        Assert.assertTrue(edorgIds.contains("edorg2"));
        Assert.assertTrue(edorgIds.contains("edorg3"));
    }

    @Test
    public void testEdOrgThroughStaff() {
        securityContextInjector.setStaffContext();

        Entity edorg1 = createEntity(EntityNames.SCHOOL, "edorg1", new HashMap<String, Object>());
        Entity edorg2 = createEntity(EntityNames.SCHOOL, "edorg2", new HashMap<String, Object>());
        Mockito.when(repo.findAll(Mockito.eq(EntityNames.EDUCATION_ORGANIZATION), Mockito.any(NeutralQuery.class))).thenReturn(Arrays.asList(edorg1), Arrays.asList(edorg2));

        Map<String, Object> sca1Body = new HashMap<String, Object>();
        sca1Body.put(ParameterConstants.STAFF_ID, "staff1");
        final Entity sca1 = createEntity(EntityNames.STAFF_COHORT_ASSOCIATION, "sca1", sca1Body);

        final Entity staff1 = createEntity(EntityNames.STAFF, "staff1", new HashMap<String, Object>());

        Mockito.when(repo.findAll(Mockito.eq(EntityNames.STAFF), argThat(new BaseMatcher<NeutralQuery>() {

            @Override
            public boolean matches(Object item) {
                NeutralQuery matching = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.OPERATOR_EQUAL, sca1.getBody().get(ParameterConstants.STAFF_ID)));
                NeutralQuery other = (NeutralQuery) item;
                return matching.getCriteria().equals(other.getCriteria());
            }

            @Override
            public void describeTo(Description description) {
                // TODO Auto-generated method stub

            }
        }))).thenReturn(Arrays.asList(staff1));

        Map<String, Object> seoa1Body = new HashMap<String, Object>();
        seoa1Body.put(ParameterConstants.STAFF_REFERENCE, "staff1");
        seoa1Body.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, "edorg1");
        Entity seoa1 = createEntity(EntityNames.STAFF_ED_ORG_ASSOCIATION, "seoa1", seoa1Body);

        Map<String, Object> seoa2Body = new HashMap<String, Object>();
        seoa2Body.put(ParameterConstants.STAFF_REFERENCE, "staff1");
        seoa2Body.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, "edorg2");
        Entity seoa2 = createEntity(EntityNames.STAFF_ED_ORG_ASSOCIATION, "seoa2", seoa2Body);

        Mockito.when(repo.findAll(Mockito.eq(EntityNames.STAFF_ED_ORG_ASSOCIATION), argThat(new BaseMatcher<NeutralQuery>() {

            @Override
            public boolean matches(Object item) {
                NeutralQuery matching = new NeutralQuery(new NeutralCriteria(ParameterConstants.STAFF_REFERENCE, NeutralCriteria.OPERATOR_EQUAL, staff1.getEntityId()));
                NeutralQuery other = (NeutralQuery) item;
                return matching.getCriteria().equals(other.getCriteria());
            }

            @Override
            public void describeTo(Description description) {
                // TODO Auto-generated method stub

            }
        }))).thenReturn(Arrays.asList(seoa1, seoa2));

        Set<String> edorgIds = arbiter.determineEdorgs(Arrays.asList(sca1), EntityNames.STAFF_COHORT_ASSOCIATION);

        Assert.assertEquals(2, edorgIds.size());
        Assert.assertTrue(edorgIds.contains("edorg1"));
        Assert.assertTrue(edorgIds.contains("edorg2"));

    }

    @Test
    (expected = AccessDeniedException.class)
    public void testInvalidReference() {
        securityContextInjector.setEducatorContext();

    Map<String, Object> body = new HashMap<String, Object>();
    body.put(ParameterConstants.SECTION_ID, "section1");
    final Entity gradebookEntry = createEntity(EntityNames.GRADEBOOK_ENTRY, "gradebookEntry", body);

        Mockito.when(repo.findAll(Mockito.eq(EntityNames.SECTION), argThat(new BaseMatcher<NeutralQuery>() {

            @Override
            public boolean matches(Object item) {
                NeutralQuery matching = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.OPERATOR_EQUAL, gradebookEntry.getBody().get(ParameterConstants.SECTION_ID)));
                NeutralQuery other = (NeutralQuery) item;
                return matching.getCriteria().equals(other.getCriteria());
            }

            @Override
            public void describeTo(Description description) {
                // TODO Auto-generated method stub

            }
        }))).thenReturn(new ArrayList<Entity>());

        Set<String> edorgIds = arbiter.determineEdorgs(Arrays.asList(gradebookEntry), EntityNames.GRADEBOOK_ENTRY);

        Assert.assertNull(edorgIds);


    }

    @Test
    public void testHierarchicalEdOrgs() {
        securityContextInjector.setStaffContext();

        Entity edorg1 = createEntity(EntityNames.SCHOOL, "edorg1", new HashMap<String, Object>());

        Mockito.when(repo.findAll(Mockito.eq(EntityNames.EDUCATION_ORGANIZATION), Mockito.any(NeutralQuery.class))).thenReturn( Arrays.asList(edorg1));
        Mockito.when(helper.getParentEdOrgs(edorg1)).thenReturn(Arrays.asList("edorg1Parent1", "edorg1Parent2"));

        Map<String, Object> attendanceBody = new HashMap<String, Object>();
        attendanceBody.put(ParameterConstants.SCHOOL_ID, "edorg1");
        Entity attendance = createEntity(EntityNames.ATTENDANCE, "attendance1", attendanceBody);

        Set<String> edorgIds = arbiter.determineHierarchicalEdorgs(Arrays.asList(attendance), EntityNames.ATTENDANCE);

        Assert.assertEquals(3, edorgIds.size());
        Assert.assertTrue(edorgIds.contains("edorg1"));
        Assert.assertTrue(edorgIds.contains("edorg1Parent1"));
        Assert.assertTrue(edorgIds.contains("edorg1Parent2"));

    }

    @Test
    public void testOrphanedEntity() {
        securityContextInjector.setStaffContext();

        Entity principalEntity = Mockito.mock(Entity.class);
        Mockito.when(principalEntity.getEntityId()).thenReturn("doofus1_id");
        SLIPrincipal principal = Mockito.mock(SLIPrincipal.class);
        Mockito.when(principal.getEntity()).thenReturn(principalEntity);
        securityContextInjector.setOauthSecurityContext(principal, false);

        Mockito.when(repo.findAll(Mockito.anyString(), Mockito.any(NeutralQuery.class))).thenReturn(new ArrayList<Entity>());

        Map<String, Object> metaData = new HashMap<String, Object>();
        metaData.put("createdBy", "doofus1_id");
        metaData.put("isOrphaned", "true");
        Entity student1 = new MongoEntity(EntityNames.STUDENT, "student1", new HashMap<String, Object>(), metaData);

        Set<String> edorgIds = arbiter.determineHierarchicalEdorgs(Arrays.asList(student1), EntityNames.STUDENT);

        Assert.assertTrue(edorgIds.isEmpty());
    }


    private Entity createEntity(String type, String id, Map<String, Object> body) {
        return new MongoEntity(type, id, body, new HashMap<String, Object>());
    }

}
