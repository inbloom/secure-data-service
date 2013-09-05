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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.APIAccessDeniedException;
import org.slc.sli.api.security.context.ContextValidator;
import org.slc.sli.api.security.roles.EntityRightsFilter;
import org.slc.sli.api.security.roles.RightAccessValidator;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.api.util.SecurityUtil;
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

    // For later cleanup.
    private static SecurityUtil.UserContext prevUserContext = SecurityUtil.getUserContext();
    private static SecurityContext prevSecurityContext = SecurityContextHolder.getContext();

    class MatchesNotAccessible extends ArgumentMatcher<Entity> {
        Set<String> studentIds = new HashSet<String>(Arrays.asList("student1", "student2", "student4", "student5", "student6",
                "student13", "student14", "student16", "student17", "student18", "student20", "student22", "student23", "student24"));

        @Override
        public boolean matches(Object arg) {
            return !studentIds.contains(((Entity) arg).getEntityId());
        }
     }

    class MatchesNotFieldAccessible extends ArgumentMatcher<Entity> {
        Set<String> studentIds = new HashSet<String>(Arrays.asList("student2", "student3", "student4", "student5", "student6", "student7", "student8",
                "student11", "student12", "student13", "student15", "student16", "student17", "student18", "student21", "student22", "student23"));

        @Override
        public boolean matches(Object arg) {
            return !studentIds.contains(((Entity) arg).getEntityId());
        }
     }

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
        SecurityUtil.setUserContext(SecurityUtil.UserContext.TEACHER_CONTEXT);
        RightAccessValidator mockAccessValidator = Mockito.mock(RightAccessValidator.class);
        Field rightAccessValidator = BasicService.class.getDeclaredField("rightAccessValidator");
        rightAccessValidator.setAccessible(true);
        rightAccessValidator.set(service, mockAccessValidator);

        Collection<GrantedAuthority> teacherContextRights = new HashSet<GrantedAuthority>(Arrays.asList(Right.TEACHER_CONTEXT, Right.READ_PUBLIC, Right.WRITE_PUBLIC));

        EntityBody entityBody1 = new EntityBody();
        entityBody1.put("studentUniqueStateId", "student1");
        EntityBody entityBody2 = new EntityBody();
        entityBody2.put("studentUniqueStateId", "student2");
        Entity entity1 = new MongoEntity("student", "student1", entityBody1, new HashMap<String,Object>());
        Entity entity2 = new MongoEntity("student", "student2", entityBody2, new HashMap<String,Object>());
        Iterable<Entity> entities = Arrays.asList(entity1, entity2);
        Mockito.when(mockRepo.findAll(Mockito.eq("student"), Mockito.any(NeutralQuery.class))).thenReturn(entities);
        Mockito.when(mockRightsFilter.makeEntityBody(Mockito.eq(entity1), Mockito.any(List.class), Mockito.any(EntityDefinition.class), Mockito.anyBoolean(), Mockito.any(Collection.class), Mockito.any(SecurityUtil.UserContext.class))).thenReturn(entityBody1);
        Mockito.when(mockRightsFilter.makeEntityBody(Mockito.eq(entity2), Mockito.any(List.class), Mockito.any(EntityDefinition.class), Mockito.anyBoolean(), Mockito.any(Collection.class), Mockito.any(SecurityUtil.UserContext.class))).thenReturn(entityBody2);

        Mockito.when(mockAccessValidator.getContextualAuthorities(Matchers.eq(false), Matchers.eq(entity1), Matchers.eq(SecurityUtil.UserContext.TEACHER_CONTEXT), Matchers.eq(true))).thenReturn(teacherContextRights);

        ContextValidator mockContextValidator = Mockito.mock(ContextValidator.class);
        Field contextValidator = BasicService.class.getDeclaredField("contextValidator");
        contextValidator.setAccessible(true);
        contextValidator.set(service, mockContextValidator);

        Map<String, SecurityUtil.UserContext> studentContext = new HashMap<String, SecurityUtil.UserContext>();
        studentContext.put(entity1.getEntityId(), SecurityUtil.UserContext.TEACHER_CONTEXT);
        studentContext.put(entity2.getEntityId(), SecurityUtil.UserContext.TEACHER_CONTEXT);

        Mockito.when(mockContextValidator.getValidatedEntityContexts(Matchers.any(EntityDefinition.class), Matchers.any(Collection.class), Matchers.anyBoolean(), Matchers.anyBoolean())).thenReturn(studentContext);

        NeutralQuery query = new NeutralQuery();
        query.setLimit(ApiQuery.API_QUERY_DEFAULT_LIMIT);
        Iterable<EntityBody> listResult = service.listBasedOnContextualRoles(query);

        List<EntityBody> bodies= new ArrayList<EntityBody>();
        for (EntityBody body : listResult) {
            bodies.add(body);
        }
        Assert.assertEquals("EntityBody mismatch", entityBody1, bodies.toArray()[0]);
        Assert.assertEquals("EntityBody mismatch", entityBody2, bodies.toArray()[1]);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testListBasedOnContextualRolesDualContext() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        securityContextInjector.setDualContext();
        SecurityUtil.setUserContext(SecurityUtil.UserContext.DUAL_CONTEXT);

        Collection<GrantedAuthority> teacherContextRights = new HashSet<GrantedAuthority>(Arrays.asList(Right.TEACHER_CONTEXT, Right.READ_PUBLIC, Right.WRITE_PUBLIC));
        Collection<GrantedAuthority> staffContextRights = new HashSet<GrantedAuthority>(Arrays.asList(Right.STAFF_CONTEXT, Right.READ_GENERAL, Right.WRITE_GENERAL));
        Collection<GrantedAuthority> dualContextRights = new HashSet<GrantedAuthority>(teacherContextRights);
        dualContextRights.addAll(staffContextRights);
        Collection<GrantedAuthority> noContextRights = new HashSet<GrantedAuthority>();

        RightAccessValidator mockAccessValidator = Mockito.mock(RightAccessValidator.class);
        Field rightAccessValidator = BasicService.class.getDeclaredField("rightAccessValidator");
        rightAccessValidator.setAccessible(true);
        rightAccessValidator.set(service, mockAccessValidator);

        List<Entity> entities = new ArrayList<Entity>();
        Map<String, SecurityUtil.UserContext> studentContext = new HashMap<String, SecurityUtil.UserContext>();
        for (int i=0; i<30; i++) {
            String id = "student" + i;
            Entity entity = new MongoEntity("student", id, new HashMap<String,Object>(), new HashMap<String,Object>());
            entities.add(entity);

            EntityBody entityBody = new EntityBody();
            entityBody.put("studentUniqueStateId", id);
            Mockito.when(mockRightsFilter.makeEntityBody(Mockito.eq(entity), Mockito.any(List.class), Mockito.any(EntityDefinition.class), Mockito.anyBoolean(),
                    Mockito.any(Collection.class), Mockito.any(SecurityUtil.UserContext.class))).thenReturn(entityBody);

            if ((i % 12) == 0) {
                studentContext.put(id, SecurityUtil.UserContext.DUAL_CONTEXT);
                Mockito.when(mockAccessValidator.getContextualAuthorities(Matchers.eq(false), Matchers.eq(entity), Matchers.eq(SecurityUtil.UserContext.DUAL_CONTEXT), Matchers.eq(true))).thenReturn(dualContextRights);
            } else if ((i % 3) == 0) {
                studentContext.put(id, SecurityUtil.UserContext.STAFF_CONTEXT);
                Mockito.when(mockAccessValidator.getContextualAuthorities(Matchers.eq(false), Matchers.eq(entity), Matchers.eq(SecurityUtil.UserContext.STAFF_CONTEXT), Matchers.eq(true))).thenReturn(staffContextRights);
            } else if ((i % 4) == 0) {
                studentContext.put(id, SecurityUtil.UserContext.TEACHER_CONTEXT);
                Mockito.when(mockAccessValidator.getContextualAuthorities(Matchers.eq(false), Matchers.eq(entity), Matchers.eq(SecurityUtil.UserContext.TEACHER_CONTEXT), Matchers.eq(true))).thenReturn(teacherContextRights);
            } else {
                studentContext.put(id, SecurityUtil.UserContext.NO_CONTEXT);
                Mockito.when(mockAccessValidator.getContextualAuthorities(Matchers.eq(false), Matchers.eq(entity), Matchers.eq(SecurityUtil.UserContext.NO_CONTEXT), Matchers.eq(true))).thenReturn(noContextRights);
            }
        }

        Mockito.when(mockRepo.findAll(Mockito.eq("student"), Mockito.any(NeutralQuery.class))).thenReturn(entities);
        Mockito.when(mockRepo.count(Mockito.eq("student"), Mockito.any(NeutralQuery.class))).thenReturn(50L);

        Mockito.doThrow(new AccessDeniedException("")).when(mockAccessValidator).checkAccess(Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.argThat(new MatchesNotAccessible()), Mockito.anyString(), Mockito.eq(staffContextRights));
        Mockito.doThrow(new AccessDeniedException("")).when(mockAccessValidator).checkFieldAccess(Mockito.any(NeutralQuery.class), Mockito.argThat(new MatchesNotFieldAccessible()), Mockito.anyString(), Mockito.eq(teacherContextRights));
        Mockito.doThrow(new AccessDeniedException("")).when(mockAccessValidator).checkAccess(Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.any(Entity.class), Mockito.anyString(), Mockito.eq(noContextRights));

        ContextValidator mockContextValidator = Mockito.mock(ContextValidator.class);
        Field contextValidator = BasicService.class.getDeclaredField("contextValidator");
        contextValidator.setAccessible(true);
        contextValidator.set(service, mockContextValidator);

        Mockito.when(mockContextValidator.getValidatedEntityContexts(Matchers.any(EntityDefinition.class), Matchers.any(Collection.class), Matchers.anyBoolean(), Matchers.anyBoolean())).thenReturn(studentContext);

        NeutralQuery query = new NeutralQuery();
        query.setLimit(ApiQuery.API_QUERY_DEFAULT_LIMIT);
        Iterable<EntityBody> listResult = service.listBasedOnContextualRoles(query);

        List<EntityBody> bodies= new ArrayList<EntityBody>();
        for (EntityBody body : listResult) {
            bodies.add(body);
        }

        Collection<String> accessibleIds = new HashSet<String>();
        for (EntityBody body : listResult) {
            accessibleIds.add((String) body.get("studentUniqueStateId"));
        }
        Assert.assertEquals(8, accessibleIds.size());
        Assert.assertTrue(accessibleIds.contains("student0"));
        Assert.assertTrue(accessibleIds.contains("student4"));
        Assert.assertTrue(accessibleIds.contains("student6"));
        Assert.assertTrue(accessibleIds.contains("student8"));
        Assert.assertTrue(accessibleIds.contains("student12"));
        Assert.assertTrue(accessibleIds.contains("student16"));
        Assert.assertTrue(accessibleIds.contains("student18"));
        Assert.assertTrue(accessibleIds.contains("student24"));
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

        Mockito.when(mockContextValidator.getValidatedEntityContexts(Matchers.any(EntityDefinition.class), Matchers.any(Collection.class), Matchers.anyBoolean(), Matchers.anyBoolean())).thenReturn(studentContext);

        Collection<GrantedAuthority> rights = service.getEntityContextAuthorities(student, isSelf, isRead);

        Assert.assertEquals(staffContextRights, rights);


        securityContextInjector.setEducatorContext();
         rights = service.getEntityContextAuthorities(student, isSelf, isRead);

        Assert.assertEquals(teacherContextRights, rights);

    }

    @Test
    public void testUserHasMultipleContextsOrDifferingRightsDualContext() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
        securityContextInjector.setDualContext();

        Method method = BasicService.class.getDeclaredMethod("userHasMultipleContextsOrDifferingRights");
        method.setAccessible(true);
        boolean testCondition = (Boolean) method.invoke(service);

        Assert.assertTrue(testCondition);
    }

    @Test
    public void testUserHasMultipleContextsOrDifferingRightsDifferentRights() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
        securityContextInjector.setEducatorContext();

        Set<GrantedAuthority> rights1 = new HashSet<GrantedAuthority>(Arrays.asList(Right.TEACHER_CONTEXT, Right.READ_PUBLIC, Right.WRITE_PUBLIC));
        Set<GrantedAuthority> rights2 = new HashSet<GrantedAuthority>(Arrays.asList(Right.TEACHER_CONTEXT, Right.READ_PUBLIC, Right.READ_GENERAL, Right.WRITE_PUBLIC));
        Map<String, Collection<GrantedAuthority>> edOrgRights = new HashMap<String, Collection<GrantedAuthority>>();
        edOrgRights.put("edOrg1", rights1);
        edOrgRights.put("edOrg2", rights2);
        SLIPrincipal principal = Mockito.mock(SLIPrincipal.class);
        Mockito.when(principal.getEdOrgRights()).thenReturn(edOrgRights);
        setPrincipalInContext(principal);

        Method method = BasicService.class.getDeclaredMethod("userHasMultipleContextsOrDifferingRights");
        method.setAccessible(true);
        boolean testCondition = (Boolean) method.invoke(service);

        Assert.assertTrue(testCondition);
    }

    @Test
    public void testUserHasMultipleContextsOrDifferingRightsSingleEdOrg() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
        securityContextInjector.setEducatorContext();

        Set<GrantedAuthority> rights1 = new HashSet<GrantedAuthority>(Arrays.asList(Right.TEACHER_CONTEXT, Right.READ_PUBLIC, Right.WRITE_PUBLIC));
        Map<String, Collection<GrantedAuthority>> edOrgRights = new HashMap<String, Collection<GrantedAuthority>>();
        edOrgRights.put("edOrg1", rights1);
        SLIPrincipal principal = Mockito.mock(SLIPrincipal.class);
        Mockito.when(principal.getEdOrgRights()).thenReturn(edOrgRights);
        setPrincipalInContext(principal);

        Method method = BasicService.class.getDeclaredMethod("userHasMultipleContextsOrDifferingRights");
        method.setAccessible(true);
        boolean testCondition = (Boolean) method.invoke(service);

        Assert.assertFalse(testCondition);
    }

    @Test
    public void testUserHasMultipleContextsOrDifferingRightsSameRights() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
        securityContextInjector.setEducatorContext();

        Set<GrantedAuthority> rights1 = new HashSet<GrantedAuthority>(Arrays.asList(Right.TEACHER_CONTEXT, Right.READ_PUBLIC, Right.WRITE_PUBLIC));
        Map<String, Collection<GrantedAuthority>> edOrgRights = new HashMap<String, Collection<GrantedAuthority>>();
        edOrgRights.put("edOrg1", rights1);
        edOrgRights.put("edOrg2", rights1);
        SLIPrincipal principal = Mockito.mock(SLIPrincipal.class);
        Mockito.when(principal.getEdOrgRights()).thenReturn(edOrgRights);
        setPrincipalInContext(principal);

        Method method = BasicService.class.getDeclaredMethod("userHasMultipleContextsOrDifferingRights");
        method.setAccessible(true);
        boolean testCondition = (Boolean) method.invoke(service);

        Assert.assertFalse(testCondition);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetAccessibleEntities() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        securityContextInjector.setDualContext();
        SecurityUtil.setUserContext(SecurityUtil.UserContext.DUAL_CONTEXT);

        List<Entity> entities = new ArrayList<Entity>();
        Map<String, SecurityUtil.UserContext> studentContext = new HashMap<String, SecurityUtil.UserContext>();
        for (int i=0; i<30; i++) {
            String id = "student" + i;
            entities.add(new MongoEntity("student", id, new HashMap<String,Object>(), new HashMap<String,Object>()));
            studentContext.put(id, SecurityUtil.UserContext.DUAL_CONTEXT);
        }
        Mockito.when(mockRepo.findAll(Mockito.eq("student"), Mockito.any(NeutralQuery.class))).thenReturn(entities);
        Mockito.when(mockRepo.count(Mockito.eq("student"), Mockito.any(NeutralQuery.class))).thenReturn(50L);

        ContextValidator mockContextValidator = Mockito.mock(ContextValidator.class);
        Field contextValidator = BasicService.class.getDeclaredField("contextValidator");
        contextValidator.setAccessible(true);
        contextValidator.set(service, mockContextValidator);
        Mockito.when(mockContextValidator.getValidatedEntityContexts(Matchers.any(EntityDefinition.class), Matchers.any(Collection.class), Matchers.anyBoolean(), Matchers.anyBoolean())).thenReturn(studentContext);

        RightAccessValidator mockAccessValidator = Mockito.mock(RightAccessValidator.class);
        Field rightAccessValidator = BasicService.class.getDeclaredField("rightAccessValidator");
        rightAccessValidator.setAccessible(true);
        rightAccessValidator.set(service, mockAccessValidator);
        Mockito.when(mockAccessValidator.getContextualAuthorities(Matchers.anyBoolean(), Matchers.any(Entity.class), Matchers.eq(SecurityUtil.UserContext.DUAL_CONTEXT), Matchers.anyBoolean())).thenReturn(new HashSet<GrantedAuthority>());
        Mockito.doThrow(new AccessDeniedException("")).when(mockAccessValidator).checkAccess(Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.argThat(new MatchesNotAccessible()), Mockito.anyString(), Mockito.anyCollection());
        Mockito.doThrow(new AccessDeniedException("")).when(mockAccessValidator).checkFieldAccess(Mockito.any(NeutralQuery.class), Mockito.argThat(new MatchesNotFieldAccessible()), Mockito.anyString(), Mockito.anyCollection());

        NeutralQuery query = new NeutralQuery();
        query.setLimit(ApiQuery.API_QUERY_DEFAULT_LIMIT);

        Method method = BasicService.class.getDeclaredMethod("getAccessibleEntities", NeutralQuery.class);
        method.setAccessible(true);
        Collection<Entity> accessibleEntities = (Collection<Entity>) method.invoke(service, query);

        Assert.assertEquals(10, accessibleEntities.size());
        Collection<String> accessibleIds = new HashSet<String>();
        for (Entity ent : accessibleEntities) {
            accessibleIds.add(ent.getEntityId());
        }
        Assert.assertTrue(accessibleIds.contains("student2"));
        Assert.assertTrue(accessibleIds.contains("student4"));
        Assert.assertTrue(accessibleIds.contains("student5"));
        Assert.assertTrue(accessibleIds.contains("student6"));
        Assert.assertTrue(accessibleIds.contains("student13"));
        Assert.assertTrue(accessibleIds.contains("student16"));
        Assert.assertTrue(accessibleIds.contains("student17"));
        Assert.assertTrue(accessibleIds.contains("student18"));
        Assert.assertTrue(accessibleIds.contains("student22"));
        Assert.assertTrue(accessibleIds.contains("student23"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetAccessibleEntitiesLimited() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        securityContextInjector.setDualContext();
        SecurityUtil.setUserContext(SecurityUtil.UserContext.DUAL_CONTEXT);

        List<Entity> entities = new ArrayList<Entity>();
        Map<String, SecurityUtil.UserContext> studentContext = new HashMap<String, SecurityUtil.UserContext>();
        for (int i=0; i<30; i++) {
            String id = "student" + i;
            entities.add(new MongoEntity("student", id, new HashMap<String,Object>(), new HashMap<String,Object>()));
            studentContext.put(id, SecurityUtil.UserContext.DUAL_CONTEXT);
        }
        Mockito.when(mockRepo.findAll(Mockito.eq("student"), Mockito.any(NeutralQuery.class))).thenReturn(entities);
        Mockito.when(mockRepo.count(Mockito.eq("student"), Mockito.any(NeutralQuery.class))).thenReturn(50L);

        ContextValidator mockContextValidator = Mockito.mock(ContextValidator.class);
        Field contextValidator = BasicService.class.getDeclaredField("contextValidator");
        contextValidator.setAccessible(true);
        contextValidator.set(service, mockContextValidator);
        Mockito.when(mockContextValidator.getValidatedEntityContexts(Matchers.any(EntityDefinition.class), Matchers.any(Collection.class), Matchers.anyBoolean(), Matchers.anyBoolean())).thenReturn(studentContext);

        RightAccessValidator mockAccessValidator = Mockito.mock(RightAccessValidator.class);
        Field rightAccessValidator = BasicService.class.getDeclaredField("rightAccessValidator");
        rightAccessValidator.setAccessible(true);
        rightAccessValidator.set(service, mockAccessValidator);
        Mockito.when(mockAccessValidator.getContextualAuthorities(Matchers.anyBoolean(), Matchers.any(Entity.class), Matchers.eq(SecurityUtil.UserContext.DUAL_CONTEXT), Matchers.anyBoolean())).thenReturn(new HashSet<GrantedAuthority>());
        Mockito.doThrow(new AccessDeniedException("")).when(mockAccessValidator).checkAccess(Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.argThat(new MatchesNotAccessible()), Mockito.anyString(), Mockito.anyCollection());
        Mockito.doThrow(new AccessDeniedException("")).when(mockAccessValidator).checkFieldAccess(Mockito.any(NeutralQuery.class), Mockito.argThat(new MatchesNotFieldAccessible()), Mockito.anyString(), Mockito.anyCollection());

        NeutralQuery query = new NeutralQuery();
        query.setLimit(5);

        Method method = BasicService.class.getDeclaredMethod("getAccessibleEntities", NeutralQuery.class);
        method.setAccessible(true);
        Collection<Entity> accessibleEntities = (Collection<Entity>) method.invoke(service, query);

        Assert.assertEquals(5, accessibleEntities.size());
        Collection<String> accessibleIds = new HashSet<String>();
        for (Entity ent : accessibleEntities) {
            accessibleIds.add(ent.getEntityId());
        }
        Assert.assertTrue(accessibleIds.contains("student2"));
        Assert.assertTrue(accessibleIds.contains("student4"));
        Assert.assertTrue(accessibleIds.contains("student5"));
        Assert.assertTrue(accessibleIds.contains("student6"));
        Assert.assertTrue(accessibleIds.contains("student13"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetAccessibleEntitiesEmpty() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        securityContextInjector.setDualContext();
        SecurityUtil.setUserContext(SecurityUtil.UserContext.DUAL_CONTEXT);

        Mockito.when(mockRepo.findAll(Mockito.eq("student"), Mockito.any(NeutralQuery.class))).thenReturn(new ArrayList<Entity>());
        Mockito.when(mockRepo.count(Mockito.eq("student"), Mockito.any(NeutralQuery.class))).thenReturn(0L);

        ContextValidator mockContextValidator = Mockito.mock(ContextValidator.class);
        Field contextValidator = BasicService.class.getDeclaredField("contextValidator");
        contextValidator.setAccessible(true);
        contextValidator.set(service, mockContextValidator);

        NeutralQuery query = new NeutralQuery();
        query.setLimit(ApiQuery.API_QUERY_DEFAULT_LIMIT);

        Method method = BasicService.class.getDeclaredMethod("getAccessibleEntities", NeutralQuery.class);
        method.setAccessible(true);
        Collection<Entity> accessibleEntities = (Collection<Entity>) method.invoke(service, query);

        Assert.assertTrue(accessibleEntities.isEmpty());
    }

    @SuppressWarnings({ "unchecked", "unused" })
    @Test
    public void testGetAccessibleEntitiesNoAccess() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        securityContextInjector.setDualContext();
        SecurityUtil.setUserContext(SecurityUtil.UserContext.DUAL_CONTEXT);

        List<Entity> entities = new ArrayList<Entity>();
        Map<String, SecurityUtil.UserContext> studentContext = new HashMap<String, SecurityUtil.UserContext>();
        for (int i=0; i<30; i++) {
            String id = "student" + i;
            entities.add(new MongoEntity("student", id, new HashMap<String,Object>(), new HashMap<String,Object>()));
            studentContext.put(id, SecurityUtil.UserContext.DUAL_CONTEXT);
        }
        Mockito.when(mockRepo.findAll(Mockito.eq("student"), Mockito.any(NeutralQuery.class))).thenReturn(entities);
        Mockito.when(mockRepo.count(Mockito.eq("student"), Mockito.any(NeutralQuery.class))).thenReturn(50L);

        ContextValidator mockContextValidator = Mockito.mock(ContextValidator.class);
        Field contextValidator = BasicService.class.getDeclaredField("contextValidator");
        contextValidator.setAccessible(true);
        contextValidator.set(service, mockContextValidator);
        Mockito.when(mockContextValidator.getValidatedEntityContexts(Matchers.any(EntityDefinition.class), Matchers.any(Collection.class), Matchers.anyBoolean(), Matchers.anyBoolean())).thenReturn(studentContext);

        RightAccessValidator mockAccessValidator = Mockito.mock(RightAccessValidator.class);
        Field rightAccessValidator = BasicService.class.getDeclaredField("rightAccessValidator");
        rightAccessValidator.setAccessible(true);
        rightAccessValidator.set(service, mockAccessValidator);
        Mockito.when(mockAccessValidator.getContextualAuthorities(Matchers.anyBoolean(), Matchers.any(Entity.class), Matchers.eq(SecurityUtil.UserContext.DUAL_CONTEXT), Matchers.anyBoolean())).thenReturn(new HashSet<GrantedAuthority>());
        Mockito.doThrow(new AccessDeniedException("")).when(mockAccessValidator).checkAccess(Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.any(Entity.class), Mockito.anyString(), Mockito.anyCollection());
        Mockito.doThrow(new AccessDeniedException("")).when(mockAccessValidator).checkFieldAccess(Mockito.any(NeutralQuery.class), Mockito.any(Entity.class), Mockito.anyString(), Mockito.anyCollection());

        NeutralQuery query = new NeutralQuery();
        query.setLimit(ApiQuery.API_QUERY_DEFAULT_LIMIT);

        Method method = BasicService.class.getDeclaredMethod("getAccessibleEntities", NeutralQuery.class);
        method.setAccessible(true);

        try {
            Collection<Entity> accessibleEntities = (Collection<Entity>) method.invoke(service, query);
        } catch (InvocationTargetException itex) {
            Assert.assertEquals(APIAccessDeniedException.class, itex.getCause().getClass());
            Assert.assertEquals("Access to resource denied.", itex.getCause().getMessage());
        }
    }


    private void setPrincipalInContext(SLIPrincipal principal) {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(principal);
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
    }


    @AfterClass
    public static void reset() {
        // Let's be good citizens and clean up after ourselves for the subsequent tests!
        SecurityUtil.setUserContext(prevUserContext);
        SecurityContextHolder.setContext(prevSecurityContext);
    }

}
