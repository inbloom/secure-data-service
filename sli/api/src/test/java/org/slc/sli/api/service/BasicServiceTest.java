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


package org.slc.sli.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.slc.sli.api.security.context.ContextValidator;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.config.BasicDefinitionStore;
import org.slc.sli.api.config.DefinitionFactory;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.roles.EntityRightsFilter;
import org.slc.sli.api.security.roles.RightAccessValidator;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.QueryParseException;
import org.slc.sli.domain.Repository;

/**
 *
 * Unit Tests
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class BasicServiceTest {
    private BasicService service = null; //class under test

    @Autowired
    private ApplicationContext context;

    @Autowired
    private SecurityContextInjector securityContextInjector;

    @Autowired
    private BasicDefinitionStore definitionStore;

    @Autowired
    DefinitionFactory factory;

    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> securityRepo;

    private Repository<Entity> mockRepo;

    private EntityRightsFilter mockRightsFilter;

    private List<Treatment> mockTreatments = new ArrayList<Treatment>();

    @SuppressWarnings("unchecked")
    @Before
    public void setup() throws IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException {
        service = (BasicService) context.getBean("basicService", "student", null, securityRepo);

        EntityDefinition student = factory.makeEntity("student")
                .exposeAs("students").build();

        service.setDefn(student);

        mockRepo = Mockito.mock(Repository.class);
        Field repo = BasicService.class.getDeclaredField("repo");
        repo.setAccessible(true);
        repo.set(service, mockRepo);

        mockRightsFilter = Mockito.mock(EntityRightsFilter.class);
        Field entityRightsFilter = BasicService.class.getDeclaredField("entityRightsFilter");
        entityRightsFilter.setAccessible(true);
        entityRightsFilter.set(service, mockRightsFilter);

        Field treatments = BasicService.class.getDeclaredField("treatments");
        treatments.setAccessible(true);
        treatments.set(service, mockTreatments);
    }

    @Test
    public void testCheckFieldAccessAdmin() {
        // inject administrator security context for unit testing
        securityContextInjector.setAdminContextWithElevatedRights();

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("economicDisadvantaged", "=", "true"));

        NeutralQuery query1 = new NeutralQuery(query);

        service.checkFieldAccess(query, false);
        assertTrue("Should match", query1.equals(query));
    }

    @Test (expected = QueryParseException.class)
    public void testCheckFieldAccessEducator() {
        // inject administrator security context for unit testing
        securityContextInjector.setEducatorContext();

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("economicDisadvantaged", "=", "true"));

        service.checkFieldAccess(query, false);
    }

    @Test
    public void testWriteSelf() {
        BasicService basicService = (BasicService) context.getBean("basicService", "teacher", new ArrayList<Treatment>(), securityRepo);
        basicService.setDefn(definitionStore.lookupByEntityType("teacher"));
        securityContextInjector.setEducatorContext("my-id");

        Map<String, Object> body = new HashMap<String, Object>();
        Entity entity = securityRepo.create("teacher", body);

        EntityBody updated = new EntityBody();
        basicService.update(entity.getEntityId(), updated);
    }

    @Test
    public void testIsSelf() {
        BasicService basicService = (BasicService) context.getBean("basicService", "teacher", new ArrayList<Treatment>(), securityRepo);
        basicService.setDefn(definitionStore.lookupByEntityType("teacher"));
        securityContextInjector.setEducatorContext("my-id");
        assertTrue(basicService.isSelf(new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.OPERATOR_EQUAL, "my-id"))));
        NeutralQuery query = new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, Arrays.asList("my-id")));
        assertTrue(basicService.isSelf(query));
        query.addCriteria(new NeutralCriteria("someOtherProperty", NeutralCriteria.OPERATOR_EQUAL, "somethingElse"));
        assertTrue(basicService.isSelf(query));
        query.addOrQuery(new NeutralQuery(new NeutralCriteria("refProperty", NeutralCriteria.OPERATOR_EQUAL, "my-id")));
        assertTrue(basicService.isSelf(query));
        query.addOrQuery(new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.OPERATOR_EQUAL, "someoneElse")));
        assertFalse(basicService.isSelf(query));
        assertFalse(basicService.isSelf(new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, Arrays.asList("my-id", "someoneElse")))));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testList() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        securityContextInjector.setEducatorContext();

        RightAccessValidator mockAccessValidator = Mockito.mock(RightAccessValidator.class);
        Field rightAccessValidator = BasicService.class.getDeclaredField("rightAccessValidator");
        rightAccessValidator.setAccessible(true);
        rightAccessValidator.set(service, mockAccessValidator);

        EntityBody entityBody1 = new EntityBody();
        entityBody1.put("studentUniqueStateId", "student1");
        EntityBody entityBody2 = new EntityBody();
        entityBody2.put("studentUniqueStateId", "student2");
        Entity entity1 = new MongoEntity("student", "student1", entityBody1, new HashMap<String,Object>());
        Entity entity2 = new MongoEntity("student", "student2", entityBody2, new HashMap<String,Object>());
        Iterable<Entity> entities = Arrays.asList(entity1, entity2);
        Mockito.when(mockRepo.findAll(Mockito.eq("student"), Mockito.any(NeutralQuery.class))).thenReturn(entities);
        Mockito.when(mockRightsFilter.makeEntityBody(Mockito.eq(entity1), Mockito.any(List.class), Mockito.any(EntityDefinition.class), Mockito.any(Collection.class), Mockito.any(Collection.class))).thenReturn(entityBody1);
        Mockito.when(mockRightsFilter.makeEntityBody(Mockito.eq(entity2), Mockito.any(List.class), Mockito.any(EntityDefinition.class), Mockito.any(Collection.class), Mockito.any(Collection.class))).thenReturn(entityBody2);

        Iterable<EntityBody> listResult = service.list(new NeutralQuery());

        List<EntityBody> bodies= new ArrayList<EntityBody>();
        for (EntityBody body : listResult) {
            bodies.add(body);
        }
        Assert.assertEquals("EntityBody mismatch", entityBody1, bodies.toArray()[0]);
        Assert.assertEquals("EntityBody mismatch", entityBody2, bodies.toArray()[1]);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testListBasedOnContextualRoles() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        securityContextInjector.setEducatorContext();

        RightAccessValidator mockAccessValidator = Mockito.mock(RightAccessValidator.class);
        Field rightAccessValidator = BasicService.class.getDeclaredField("rightAccessValidator");
        rightAccessValidator.setAccessible(true);
        rightAccessValidator.set(service, mockAccessValidator);

        Collection<GrantedAuthority> teacherContextRights = SecurityUtil.getSLIPrincipal().getEdOrgContextRights().get(SecurityContextInjector.ED_ORG_ID).get(SecurityUtil.UserContext.TEACHER_CONTEXT);

        EntityBody entityBody1 = new EntityBody();
        entityBody1.put("studentUniqueStateId", "student1");
        EntityBody entityBody2 = new EntityBody();
        entityBody2.put("studentUniqueStateId", "student2");
        Entity entity1 = new MongoEntity("student", "student1", entityBody1, new HashMap<String,Object>());
        Entity entity2 = new MongoEntity("student", "student2", entityBody2, new HashMap<String,Object>());
        Iterable<Entity> entities = Arrays.asList(entity1, entity2);
        Mockito.when(mockRepo.findAll(Mockito.eq("student"), Mockito.any(NeutralQuery.class))).thenReturn(entities);
        Mockito.when(mockRightsFilter.makeEntityBody(Mockito.eq(entity1), Mockito.any(List.class), Mockito.any(EntityDefinition.class), Mockito.anyBoolean(), Mockito.any(Collection.class))).thenReturn(entityBody1);
        Mockito.when(mockRightsFilter.makeEntityBody(Mockito.eq(entity2), Mockito.any(List.class), Mockito.any(EntityDefinition.class), Mockito.anyBoolean(), Mockito.any(Collection.class))).thenReturn(entityBody2);

        Mockito.when(mockAccessValidator.getContextualAuthorities(Matchers.eq(false), Matchers.eq(entity1), Matchers.eq(SecurityUtil.UserContext.TEACHER_CONTEXT), Matchers.eq(true))).thenReturn(teacherContextRights);

        ContextValidator mockContextValidator = Mockito.mock(ContextValidator.class);
        Field contextValidator = BasicService.class.getDeclaredField("contextValidator");
        contextValidator.setAccessible(true);
        contextValidator.set(service, mockContextValidator);

        Map<String, SecurityUtil.UserContext> studentContext = new HashMap<String, SecurityUtil.UserContext>();
        studentContext.put(entity1.getEntityId(), SecurityUtil.UserContext.TEACHER_CONTEXT);
        studentContext.put(entity2.getEntityId(), SecurityUtil.UserContext.TEACHER_CONTEXT);

        Mockito.when(mockContextValidator.getValidatedEntityContexts(Matchers.any(EntityDefinition.class), Matchers.any(Collection.class), Matchers.anyBoolean())).thenReturn(studentContext);

        Iterable<EntityBody> listResult = service.listBasedOnContextualRoles(new NeutralQuery());

        List<EntityBody> bodies= new ArrayList<EntityBody>();
        for (EntityBody body : listResult) {
            bodies.add(body);
        }
        Assert.assertEquals("EntityBody mismatch", entityBody1, bodies.toArray()[0]);
        Assert.assertEquals("EntityBody mismatch", entityBody2, bodies.toArray()[1]);

        securityContextInjector.setDualContext();

        Collection<GrantedAuthority> staffContextRights = SecurityUtil.getSLIPrincipal().getEdOrgContextRights().get(SecurityContextInjector.ED_ORG_ID).get(SecurityUtil.UserContext.STAFF_CONTEXT);
        studentContext.put(entity2.getEntityId(), SecurityUtil.UserContext.DUAL_CONTEXT);

        Mockito.when(mockAccessValidator.getContextualAuthorities(Matchers.eq(false), Matchers.eq(entity1), Matchers.eq(SecurityUtil.UserContext.DUAL_CONTEXT), Matchers.eq(true))).thenReturn(staffContextRights);
        studentContext.remove(entity1.getEntityId());

        Mockito.when(mockContextValidator.getValidatedEntityContexts(Matchers.any(EntityDefinition.class), Matchers.any(Collection.class), Matchers.anyBoolean())).thenReturn(studentContext);

        listResult = service.listBasedOnContextualRoles(new NeutralQuery());

        bodies.clear();
        for (EntityBody body : listResult) {
            bodies.add(body);
        }

        Assert.assertEquals("Return result is more than 1", 1, bodies.size());
        Assert.assertEquals("EntityBody mismatch", entityBody2, bodies.toArray()[0]);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreate() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        securityContextInjector.setEducatorContext();

        RightAccessValidator mockAccessValidator = Mockito.mock(RightAccessValidator.class);
        Field rightAccessValidator = BasicService.class.getDeclaredField("rightAccessValidator");
        rightAccessValidator.setAccessible(true);
        rightAccessValidator.set(service, mockAccessValidator);

        EntityBody entityBody1 = new EntityBody();
        entityBody1.put("studentUniqueStateId", "student1");
        EntityBody entityBody2 = new EntityBody();
        entityBody2.put("studentUniqueStateId", "student2");
        List<EntityBody> entityBodies = Arrays.asList(entityBody1, entityBody2);
        Entity entity1 = new MongoEntity("student", "student1", entityBody1, new HashMap<String,Object>());
        Entity entity2 = new MongoEntity("student", "student2", entityBody2, new HashMap<String,Object>());
        Mockito.when(mockRepo.create(Mockito.eq("student"), Mockito.eq(entityBody1), Mockito.any(Map.class), Mockito.eq("student"))).thenReturn(entity1);
        Mockito.when(mockRepo.create(Mockito.eq("student"), Mockito.eq(entityBody2), Mockito.any(Map.class), Mockito.eq("student"))).thenReturn(entity2);

        List<String> listResult = service.create(entityBodies);

        Assert.assertEquals("EntityBody mismatch", entity1.getEntityId(), listResult.toArray()[0]);
        Assert.assertEquals("EntityBody mismatch", entity2.getEntityId(), listResult.toArray()[1]);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateBasedOnContextualRoles() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        securityContextInjector.setEducatorContext();

        RightAccessValidator mockAccessValidator = Mockito.mock(RightAccessValidator.class);
        Field rightAccessValidator = BasicService.class.getDeclaredField("rightAccessValidator");
        rightAccessValidator.setAccessible(true);
        rightAccessValidator.set(service, mockAccessValidator);

        EntityBody entityBody1 = new EntityBody();
        entityBody1.put("studentUniqueStateId", "student1");
        EntityBody entityBody2 = new EntityBody();
        entityBody2.put("studentUniqueStateId", "student2");
        List<EntityBody> entityBodies = Arrays.asList(entityBody1, entityBody2);
        Entity entity1 = new MongoEntity("student", "student1", entityBody1, new HashMap<String,Object>());
        Entity entity2 = new MongoEntity("student", "student2", entityBody2, new HashMap<String,Object>());
        Mockito.when(mockRepo.create(Mockito.eq("student"), Mockito.eq(entityBody1), Mockito.any(Map.class), Mockito.eq("student"))).thenReturn(entity1);
        Mockito.when(mockRepo.create(Mockito.eq("student"), Mockito.eq(entityBody2), Mockito.any(Map.class), Mockito.eq("student"))).thenReturn(entity2);

        List<String> listResult = service.createBasedOnContextualRoles(entityBodies);

        Assert.assertEquals("EntityBody mismatch", entity1.getEntityId(), listResult.toArray()[0]);
        Assert.assertEquals("EntityBody mismatch", entity2.getEntityId(), listResult.toArray()[1]);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateBasedOnContextualRoles() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        securityContextInjector.setStaffContext();
        service = (BasicService) context.getBean("basicService", "student", new ArrayList<Treatment>(), securityRepo);
        EntityDefinition studentDef = factory.makeEntity("student").exposeAs("students").build();
        service.setDefn(studentDef);
        EntityBody entityBody1 = new EntityBody();
        entityBody1.put("studentUniqueStateId", "student1");
        Entity student = securityRepo.create(EntityNames.STUDENT, entityBody1);
        EntityBody entityBody2 = new EntityBody();
        entityBody2.put(ParameterConstants.STUDENT_ID, student.getEntityId());
        entityBody2.put(ParameterConstants.SCHOOL_ID, SecurityContextInjector.ED_ORG_ID);
        securityRepo.create(EntityNames.STUDENT_SCHOOL_ASSOCIATION, entityBody2);
        securityRepo.createWithRetries(EntityNames.EDUCATION_ORGANIZATION,SecurityContextInjector.ED_ORG_ID, new HashMap<String, Object>(), new HashMap<String, Object>(), EntityNames.EDUCATION_ORGANIZATION, 1);

        EntityBody putEntity = new EntityBody();
        putEntity.put("studentUniqueStateId", "student1");
        putEntity.put("loginId", "student1");
        boolean result = service.updateBasedOnContextualRoles(student.getEntityId(), putEntity);

        Assert.assertTrue(result);
        Entity studentResult = securityRepo.findById(EntityNames.STUDENT, student.getEntityId());
        Assert.assertNotNull(studentResult.getBody());
        Assert.assertNotNull(studentResult.getBody().get("studentUniqueStateId"));
        Assert.assertEquals("student1", studentResult.getBody().get("studentUniqueStateId"));
        Assert.assertNotNull(studentResult.getBody().get("loginId"));
        Assert.assertEquals("student1",studentResult.getBody().get("loginId"));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = AccessDeniedException.class)
    public void testUpdateBasedOnContextualRolesAccessDenied() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        securityContextInjector.setEducatorContext();
        service = (BasicService) context.getBean("basicService", "student", new ArrayList<Treatment>(), securityRepo);
        EntityDefinition studentDef = factory.makeEntity("student").exposeAs("students").build();
        service.setDefn(studentDef);
        EntityBody entityBody1 = new EntityBody();
        entityBody1.put("studentUniqueStateId", "student1");
        Entity student = securityRepo.create(EntityNames.STUDENT, entityBody1);
        EntityBody entityBody2 = new EntityBody();
        entityBody2.put(ParameterConstants.STUDENT_ID, student.getEntityId());
        entityBody2.put(ParameterConstants.SCHOOL_ID, SecurityContextInjector.ED_ORG_ID);
        securityRepo.create(EntityNames.STUDENT_SCHOOL_ASSOCIATION, entityBody2);
        securityRepo.createWithRetries(EntityNames.EDUCATION_ORGANIZATION,SecurityContextInjector.ED_ORG_ID, new HashMap<String, Object>(), new HashMap<String, Object>(), EntityNames.EDUCATION_ORGANIZATION, 1);

        EntityBody putEntity = new EntityBody();
        putEntity.put("studentUniqueStateId", "student1");
        putEntity.put("loginId", "student1");
        boolean result = service.updateBasedOnContextualRoles(student.getEntityId(), putEntity);

        Assert.assertFalse(result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPatchBasedOnContextualRoles() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        securityContextInjector.setStaffContext();
        service = (BasicService) context.getBean("basicService", "student", new ArrayList<Treatment>(), securityRepo);
        EntityDefinition studentDef = factory.makeEntity("student").exposeAs("students").build();
        service.setDefn(studentDef);

        EntityBody studentBody = new EntityBody();
        studentBody.put("studentUniqueStateId", "123");
        Entity student = securityRepo.create(EntityNames.STUDENT, studentBody);

        EntityBody ssaBody = new EntityBody();
        ssaBody.put(ParameterConstants.STUDENT_ID, student.getEntityId());
        ssaBody.put(ParameterConstants.SCHOOL_ID, SecurityContextInjector.ED_ORG_ID);
        securityRepo.create(EntityNames.STUDENT_SCHOOL_ASSOCIATION, ssaBody);

        securityRepo.createWithRetries(EntityNames.EDUCATION_ORGANIZATION,SecurityContextInjector.ED_ORG_ID, new HashMap<String, Object>(), new HashMap<String, Object>(), EntityNames.EDUCATION_ORGANIZATION, 1);

        EntityBody putEntity = new EntityBody();
        putEntity.put("studentUniqueStateId", "456");
        putEntity.put("schoolFoodServicesEligibility", "Yes");

        boolean result = service.patchBasedOnContextualRoles(student.getEntityId(), putEntity);

        Assert.assertTrue(result);
        Entity studentResult = securityRepo.findById(EntityNames.STUDENT, student.getEntityId());
        Assert.assertNotNull(studentResult.getBody());
        Assert.assertNotNull(studentResult.getBody().get("studentUniqueStateId"));
        Assert.assertEquals("456", studentResult.getBody().get("studentUniqueStateId"));
        Assert.assertNotNull(studentResult.getBody().get("schoolFoodServicesEligibility"));
        Assert.assertEquals("Yes",studentResult.getBody().get("schoolFoodServicesEligibility"));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = AccessDeniedException.class)
    public void testPatchBasedOnContextualRolesAccessDenied() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        securityContextInjector.setEducatorContext();
        service = (BasicService) context.getBean("basicService", "student", new ArrayList<Treatment>(), securityRepo);

        EntityDefinition studentDef = factory.makeEntity("student").exposeAs("students").build();
        service.setDefn(studentDef);

        EntityBody studentBody = new EntityBody();
        studentBody.put("studentUniqueStateId", "123");
        Entity student = securityRepo.create(EntityNames.STUDENT, studentBody);

        EntityBody ssaBody = new EntityBody();
        ssaBody.put(ParameterConstants.STUDENT_ID, student.getEntityId());
        ssaBody.put(ParameterConstants.SCHOOL_ID, SecurityContextInjector.ED_ORG_ID);
        securityRepo.create(EntityNames.STUDENT_SCHOOL_ASSOCIATION, ssaBody);

        securityRepo.createWithRetries(EntityNames.EDUCATION_ORGANIZATION,SecurityContextInjector.ED_ORG_ID, new HashMap<String, Object>(), new HashMap<String, Object>(), EntityNames.EDUCATION_ORGANIZATION, 1);

        EntityBody putEntity = new EntityBody();
        putEntity.put("studentUniqueStateId", "456");
        putEntity.put("schoolFoodServicesEligibility", "Yes");

        boolean result = service.patchBasedOnContextualRoles(student.getEntityId(), putEntity);

        Assert.assertFalse(result);
    }


    @Test
    public void testgetEntityContextAuthorities() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException {
        securityContextInjector.setDualContext();

        RightAccessValidator mockAccessValidator = Mockito.mock(RightAccessValidator.class);
        Field rightAccessValidator = BasicService.class.getDeclaredField("rightAccessValidator");
        rightAccessValidator.setAccessible(true);
        rightAccessValidator.set(service, mockAccessValidator);

        boolean isSelf = true;
        boolean isRead = true;

        EntityBody entityBody1 = new EntityBody();
        entityBody1.put("studentUniqueStateId", "student1");
        Entity student = securityRepo.create(EntityNames.STUDENT, entityBody1);

        Collection<GrantedAuthority> staffContextRights = SecurityUtil.getSLIPrincipal().getEdOrgContextRights().get(SecurityContextInjector.ED_ORG_ID).get(SecurityUtil.UserContext.STAFF_CONTEXT);
        Collection<GrantedAuthority> teacherContextRights = SecurityUtil.getSLIPrincipal().getEdOrgContextRights().get(SecurityContextInjector.ED_ORG_ID).get(SecurityUtil.UserContext.TEACHER_CONTEXT);


        Mockito.when(mockAccessValidator.getContextualAuthorities(Matchers.eq(isSelf), Matchers.eq(student), Matchers.eq(SecurityUtil.UserContext.STAFF_CONTEXT), Matchers.eq(isRead))).thenReturn(staffContextRights);
        Mockito.when(mockAccessValidator.getContextualAuthorities(Matchers.eq(isSelf), Matchers.eq(student), Matchers.eq(SecurityUtil.UserContext.TEACHER_CONTEXT), Matchers.eq(isRead))).thenReturn(teacherContextRights);

        ContextValidator mockContextValidator = Mockito.mock(ContextValidator.class);
        Field contextValidator = BasicService.class.getDeclaredField("contextValidator");
        contextValidator.setAccessible(true);
        contextValidator.set(service, mockContextValidator);

        Map<String, SecurityUtil.UserContext> studentContext = new HashMap<String, SecurityUtil.UserContext>();
        studentContext.put(student.getEntityId(), SecurityUtil.UserContext.STAFF_CONTEXT);

        Mockito.when(mockContextValidator.getValidatedEntityContexts(Matchers.any(EntityDefinition.class), Matchers.any(Collection.class), Matchers.anyBoolean())).thenReturn(studentContext);

        Collection<GrantedAuthority> rights = service.getEntityContextAuthorities(student, isSelf, isRead);

        Assert.assertEquals(staffContextRights, rights);


        securityContextInjector.setEducatorContext();
         rights = service.getEntityContextAuthorities(student, isSelf, isRead);

        Assert.assertEquals(teacherContextRights, rights);

    }

}
