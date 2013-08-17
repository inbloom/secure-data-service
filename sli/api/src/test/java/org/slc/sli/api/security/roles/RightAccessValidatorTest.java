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
package org.slc.sli.api.security.roles;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.api.security.EdOrgContextRightsCache;
import org.slc.sli.api.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.QueryParseException;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;



/**
 * @author: tke
 */


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class RightAccessValidatorTest {

    @Autowired
    SecurityContextInjector securityContextInjector;

    @Autowired
    private RightAccessValidator service;

    private static final String STUDENT_ID = "testStudentId";

    private static final String BAD_EDORG = "RandomEdorgId";
    private static final String BAD_STUDENT = "badStudentId";

    private static final Collection<GrantedAuthority> EDU_AUTHS = new HashSet<GrantedAuthority>(Arrays.asList(Right.READ_PUBLIC, Right.READ_GENERAL));
    private static final Collection<GrantedAuthority> ADMIN_AUTHS = new HashSet<GrantedAuthority>(Arrays.asList(Right.READ_PUBLIC, Right.READ_RESTRICTED));
    private static final Collection<GrantedAuthority> ALL_AUTHS = new HashSet<GrantedAuthority>(Arrays.asList(Right.READ_PUBLIC, Right.READ_GENERAL, Right.READ_RESTRICTED));
    private static final Collection<GrantedAuthority> NO_AUTHS = new HashSet<GrantedAuthority>();

    @Autowired
    Repository mockRepo;

    private EntityEdOrgRightBuilder entityEdOrgRightBuilder;

    @Before
    public void setup() throws IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException {
        entityEdOrgRightBuilder = Mockito.mock(EntityEdOrgRightBuilder.class);
        Field rightBuilder = RightAccessValidator.class.getDeclaredField("entityEdOrgRightBuilder");
        rightBuilder.setAccessible(true);
        rightBuilder.set(service, entityEdOrgRightBuilder);
    }

    @Test
    public void testCheckFieldAccessAdmin() {
        // inject administrator security context for unit testing

        securityContextInjector.setAdminContextWithElevatedRights();

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("economicDisadvantaged", "=", "true"));

        NeutralQuery query1 = new NeutralQuery(query);


        service.checkFieldAccess(query, false, null, EntityNames.STUDENT, service.getContextualAuthorities(false, null, SecurityUtil.UserContext.STAFF_CONTEXT,false));
        assertTrue("Should match", query1.equals(query));
    }

    @Test (expected = QueryParseException.class)
    public void testCheckFieldAccessEducator() {
        // inject administrator security context for unit testing
        securityContextInjector.setEducatorContext();

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("economicDisadvantaged", "=", "true"));

        mockRepo.createWithRetries(EntityNames.EDUCATION_ORGANIZATION, SecurityContextInjector.ED_ORG_ID, new HashMap<String, Object>(),
                new HashMap<String, Object>(), EntityNames.EDUCATION_ORGANIZATION, 1);

        mockRepo.create(EntityNames.STUDENT_SCHOOL_ASSOCIATION, createStudentSchoolAssociation(STUDENT_ID, SecurityContextInjector.ED_ORG_ID));

        Entity student = createEntity(EntityNames.STUDENT, STUDENT_ID, new HashMap<String, Object>());
        mockRepo.createWithRetries(EntityNames.STUDENT, STUDENT_ID, new HashMap<String, Object>(), new HashMap<String, Object>(), EntityNames.STUDENT, 1);
        service.checkFieldAccess(query, false, student, EntityNames.STUDENT, service.getContextualAuthorities(false, student, SecurityUtil.UserContext.TEACHER_CONTEXT,false));
    }

    @Test
    public void testCheckFullAccessAdmin() {
        securityContextInjector.setAdminContextWithElevatedRights();

        service.checkAccess(true, false, null, EntityNames.STUDENT, service.getContextualAuthorities(false, null, SecurityUtil.UserContext.STAFF_CONTEXT, false));
    }

    @Test
    public void testCheckAccessAdmin() {
        securityContextInjector.setLeaAdminContext();
        service.checkAccess(true, false, null, EntityNames.ADMIN_DELEGATION, service.getContextualAuthorities(false, null, SecurityUtil.UserContext.STAFF_CONTEXT, false));
    }

    @Test (expected = AccessDeniedException.class)
    public void testCheckAccessStaffReadAccessDenied() {
        securityContextInjector.setStaffContext();

        mockRepo.createWithRetries(EntityNames.EDUCATION_ORGANIZATION, BAD_EDORG, new HashMap<String, Object>(),
                new HashMap<String, Object>(), EntityNames.EDUCATION_ORGANIZATION, 1);

        mockRepo.create(EntityNames.STUDENT_SCHOOL_ASSOCIATION, createStudentSchoolAssociation(BAD_STUDENT, BAD_EDORG));

        Map<String, Object> eb = new HashMap<String, Object>();
        eb.put("studentUniqueStateId", "1234");
        Entity student = createEntity(EntityNames.STUDENT, BAD_STUDENT, eb);

        service.checkAccess(false, false, student, EntityNames.STUDENT, service.getContextualAuthorities(false, student, SecurityUtil.UserContext.STAFF_CONTEXT, false));
    }

    @Test (expected = AccessDeniedException.class)
    public void testCheckAccessStaffWriteAccessDenied() {
        securityContextInjector.setStaffContext();

        mockRepo.createWithRetries(EntityNames.EDUCATION_ORGANIZATION, BAD_EDORG, new HashMap<String, Object>(),
                new HashMap<String, Object>(), EntityNames.EDUCATION_ORGANIZATION, 1);

        mockRepo.create(EntityNames.STUDENT_SCHOOL_ASSOCIATION, createStudentSchoolAssociation(BAD_STUDENT, BAD_EDORG));

        Map<String, Object> eb = new HashMap<String, Object>();
        eb.put("studentUniqueStateId", "1234");
        Entity student = createEntity(EntityNames.STUDENT, BAD_STUDENT, eb);

        service.checkAccess(true, false, student, EntityNames.STUDENT, service.getContextualAuthorities(false, student, SecurityUtil.UserContext.STAFF_CONTEXT,false));
    }

    @Test
    public void testCheckAccessStaffWrite() {
        securityContextInjector.setStaffContext();

        mockRepo.createWithRetries(EntityNames.EDUCATION_ORGANIZATION, SecurityContextInjector.ED_ORG_ID, new HashMap<String, Object>(),
                new HashMap<String, Object>(), EntityNames.EDUCATION_ORGANIZATION, 1);

        mockRepo.create(EntityNames.STUDENT_SCHOOL_ASSOCIATION, createStudentSchoolAssociation(STUDENT_ID, SecurityContextInjector.ED_ORG_ID));

        Entity student = createEntity(EntityNames.STUDENT, STUDENT_ID, new HashMap<String, Object>());

        service.checkAccess(false, false, student, EntityNames.STUDENT, service.getContextualAuthorities(false, student, SecurityUtil.UserContext.STAFF_CONTEXT,false));
    }

    @Test
    public void testGetContextualAuthoritiesStaffOrphan() {
        String principalId = "SuperTeacher1";
        Entity princEntity = new MongoEntity(null, principalId, new HashMap<String,Object>(), new HashMap<String,Object>());
        SLIPrincipal principal = new SLIPrincipal();
        principal.setEntity(princEntity);
        principal.setUserType(EntityNames.STAFF);

        EdOrgContextRightsCache edOrgContextRights = createEdOrgContextRights();
        principal.setEdOrgContextRights(edOrgContextRights);

        securityContextInjector.setOauthSecurityContext(principal, false);

        Map<String,Object> metaData = new HashMap<String,Object>();
        metaData.put("createdBy", principalId);
        metaData.put("isOrphaned", "true");
        Entity entity = new MongoEntity("student", null, new HashMap<String,Object>(), metaData);

        Collection<GrantedAuthority> auths = service.getContextualAuthorities(false, entity, SecurityUtil.UserContext.STAFF_CONTEXT,false);

        Assert.assertEquals("Expected all rights", ALL_AUTHS, auths);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetContextualAuthoritiesStaffMatchingEdOrgs() {
        Entity princEntity = new MongoEntity(null, "SuperTeacher2", new HashMap<String,Object>(), new HashMap<String,Object>());
        SLIPrincipal principal = new SLIPrincipal();
        principal.setEntity(princEntity);
        principal.setUserType(EntityNames.STAFF);

        EdOrgContextRightsCache edOrgContextRights = createEdOrgContextRights();
        principal.setEdOrgContextRights(edOrgContextRights);

        securityContextInjector.setOauthSecurityContext(principal, false);

        Entity student = createEntity(EntityNames.STUDENT, STUDENT_ID, new HashMap<String, Object>());

        Mockito.when(entityEdOrgRightBuilder.buildEntityEdOrgContextRights(edOrgContextRights, student, SecurityUtil.UserContext.STAFF_CONTEXT,false)).thenReturn(ADMIN_AUTHS);

        Collection<GrantedAuthority> auths = service.getContextualAuthorities(false, student, SecurityUtil.UserContext.STAFF_CONTEXT, false);

        Assert.assertEquals("Expected administrator rights", ADMIN_AUTHS, auths);
    }

    @Test
    public void testGetContextualAuthoritiesStaffNoMatchingEdOrgs() {
        Map<String, Collection<GrantedAuthority>> edOrgRights = new HashMap<String, Collection<GrantedAuthority>>();
        Entity princEntity = new MongoEntity(null, "RegularTeacher1", new HashMap<String,Object>(), new HashMap<String,Object>());
        SLIPrincipal principal = new SLIPrincipal();
        principal.setEntity(princEntity);
        principal.setUserType(EntityNames.STAFF);
        securityContextInjector.setOauthSecurityContext(principal, false);

        Entity student = createEntity(EntityNames.STUDENT, STUDENT_ID, new HashMap<String, Object>());

        Mockito.when(entityEdOrgRightBuilder.buildEntityEdOrgRights(edOrgRights, student, false)).thenReturn(NO_AUTHS);

        Collection<GrantedAuthority> auths = service.getContextualAuthorities(false, student, SecurityUtil.UserContext.STAFF_CONTEXT,false);

        Assert.assertEquals("Expected no rights", NO_AUTHS, auths);
    }

    @Test
    public void testGetContextualAuthoritiesNonStaff() {
        String token = "AQIC5wM2LY4SfczsoqTgHpfSEciO4J34Hc5ThvD0QaM2QUI.*AAJTSQACMDE.*";
        Entity princEntity = new MongoEntity(null, "RegularTeacher2", new HashMap<String,Object>(), new HashMap<String,Object>());
        SLIPrincipal principal = new SLIPrincipal();
        principal.setUserType(EntityNames.TEACHER);
        principal.setEntity(princEntity);
        PreAuthenticatedAuthenticationToken authenticationToken = new PreAuthenticatedAuthenticationToken(principal, token, EDU_AUTHS);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        Entity entity = new MongoEntity("student", null, new HashMap<String,Object>(), new HashMap<String,Object>());

        Collection<GrantedAuthority> auths = service.getContextualAuthorities(false, entity, SecurityUtil.UserContext.TEACHER_CONTEXT,false);

        Assert.assertEquals("Expected educator rights", EDU_AUTHS, auths);
    }

    @Test
    public void testGetContextualAuthoritiesNonStaffSelf() {
        String token = "AQIC5wM2LY4SfczsoqTgHpfSEciO4J34Hc5ThvD0QaM2QUI.*AAJTSQACMDE.*";
        Entity princEntity = new MongoEntity(null, "RegularTeacher3", new HashMap<String,Object>(), new HashMap<String,Object>());
        SLIPrincipal principal = new SLIPrincipal();
        principal.setUserType(EntityNames.TEACHER);
        principal.setSelfRights(ADMIN_AUTHS);
        PreAuthenticatedAuthenticationToken authenticationToken = new PreAuthenticatedAuthenticationToken(principal, token, EDU_AUTHS);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        Entity entity = new MongoEntity("teacher", null, new HashMap<String,Object>(), new HashMap<String,Object>());

        Collection<GrantedAuthority> auths = service.getContextualAuthorities(true, entity, SecurityUtil.UserContext.TEACHER_CONTEXT,false);

        Assert.assertEquals("Expected all rights", ALL_AUTHS, auths);
    }

    @Test
    public void testGetContextualAuthoritiesNullEntity() {
        securityContextInjector.setEducatorContext();

        Collection<GrantedAuthority> auths = service.getContextualAuthorities(false, null, SecurityUtil.UserContext.TEACHER_CONTEXT,false);

        Assert.assertEquals("Expected no rights", NO_AUTHS, auths);
    }


    private Entity createEntity(String type, String id, Map<String, Object> body) {
        return new MongoEntity(type, id, body, new HashMap<String,Object>());
    }

    private Map<String, String> createStudentSchoolAssociation(String studentId, String schoolId) {
        Map<String, String> entity = new HashMap<String, String>();
        entity.put(ParameterConstants.STUDENT_ID, studentId);
        entity.put(ParameterConstants.SCHOOL_ID, schoolId);
        return  entity;
    }

    private Map<String, String> createEdorg(String edorgId) {
        Map<String, String> entity = new HashMap<String, String>();
        entity.put(ParameterConstants.STATE_ORGANIZATION_ID, edorgId);
        return  entity;
    }

    private EdOrgContextRightsCache createEdOrgContextRights() {
        EdOrgContextRightsCache edOrgContextRights = new EdOrgContextRightsCache();
        Collection<GrantedAuthority> authorities2S = new HashSet<GrantedAuthority>(ADMIN_AUTHS);
        Collection<GrantedAuthority> authorities2T = new HashSet<GrantedAuthority>(EDU_AUTHS);

        Map<String, Collection<GrantedAuthority>> contextRights2 = new HashMap<String, Collection<GrantedAuthority>>();
        contextRights2.put(Right.STAFF_CONTEXT.name(), authorities2S);

        Map<String, Collection<GrantedAuthority>> contextRights3 = new HashMap<String, Collection<GrantedAuthority>>();
        contextRights3.put(Right.TEACHER_CONTEXT.name(), authorities2T);

        edOrgContextRights.put("edOrg1", contextRights2);
        edOrgContextRights.put("edOrg2", contextRights3);

        return edOrgContextRights;
    }
}
