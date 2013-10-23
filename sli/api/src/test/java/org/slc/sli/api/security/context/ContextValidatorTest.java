package org.slc.sli.api.security.context;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.common.constants.EntityNames;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.context.validator.AbstractContextValidator;
import org.slc.sli.api.security.context.validator.IContextValidator;
import org.slc.sli.api.security.context.validator.StaffTeacherToStaffTeacherValidator;
import org.slc.sli.api.security.context.validator.StaffToStudentValidator;
import org.slc.sli.api.security.context.validator.TeacherToStudentValidator;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;

public class ContextValidatorTest {

    private ContextValidator contextValidator;

    private List<IContextValidator> validators;
    private PagingRepositoryDelegate<Entity> repo;
    private EntityOwnershipValidator ownership;
    private EdOrgHelper edOrgHelper;

    // For later cleanup.
    private static SecurityUtil.UserContext prevUserContext = SecurityUtil.getUserContext();
    private static SecurityContext prevSecurityContext = SecurityContextHolder.getContext();

    class MatchesStaffStudentIds extends ArgumentMatcher<Set<String>> {
        Set<String> studentIds = new HashSet<String>(Arrays.asList("student1", "student2", "student3", "student5"));

        @Override
        @SuppressWarnings("unchecked")
        public boolean matches(Object arg) {
            return studentIds.containsAll((Set<String>)arg);
        }
     }

    class MatchesTeacherStudentIds extends ArgumentMatcher<Set<String>> {
        Set<String> studentIds = new HashSet<String>(Arrays.asList("student2", "student4", "student5"));

        @Override
        @SuppressWarnings("unchecked")
        public boolean matches(Object arg) {
            return studentIds.containsAll((Set<String>)arg);
        }
     }

    class MatchesSomeStudentIds extends ArgumentMatcher<Set<String>> {
        Set<String> studentIds = new HashSet<String>(Arrays.asList("student0", "student1", "student2"));

        @Override
        @SuppressWarnings("unchecked")
        public boolean matches(Object arg) {
            return studentIds.containsAll((Set<String>)arg);
        }
     }

    class MatchesAllStudentIds extends ArgumentMatcher<Set<String>> {
        Set<String> studentIds = new HashSet<String>(Arrays.asList("student1", "student2", "student3", "student4", "student5"));

        @Override
        @SuppressWarnings("unchecked")
        public boolean matches(Object arg) {
            return studentIds.containsAll((Set<String>)arg);
        }
     }

    @SuppressWarnings("unchecked")
    @Before
    public void setup() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        contextValidator = new ContextValidator();

        SecurityUtil.setUserContext(SecurityUtil.UserContext.DUAL_CONTEXT);

        AbstractContextValidator stsValidator = Mockito.mock(StaffToStudentValidator.class);
        Mockito.when(stsValidator.canValidate(Mockito.matches("student"), Mockito.anyBoolean())).thenReturn(true);
        Set<String> staffStudentIds = new HashSet<String>(Arrays.asList("student1", "student2", "student3", "student5"));
        Set<String> someStaffStudentIds = new HashSet<String>(Arrays.asList("student1", "student2"));
        Mockito.when(stsValidator.validate(Mockito.matches("student"), Mockito.argThat(new MatchesStaffStudentIds()))).thenReturn(staffStudentIds);
        Mockito.when(stsValidator.validate(Mockito.matches("student"), Mockito.argThat(new MatchesSomeStudentIds()))).thenReturn(someStaffStudentIds);
        Mockito.when(stsValidator.validate(Mockito.matches("student"), Mockito.argThat(new MatchesAllStudentIds()))).thenReturn(staffStudentIds);
        Mockito.when(stsValidator.getContext()).thenReturn(SecurityUtil.UserContext.STAFF_CONTEXT);

        AbstractContextValidator ttsValidator = Mockito.mock(TeacherToStudentValidator.class);
        Mockito.when(ttsValidator.canValidate(Mockito.matches("student"), Mockito.anyBoolean())).thenReturn(true);
        Set<String> teacherStudentIds = new HashSet<String>(Arrays.asList("student2", "student4", "student5"));
        Set<String> someTeacherStudentIds = new HashSet<String>(Arrays.asList("student2"));
        Mockito.when(ttsValidator.validate(Mockito.matches("student"), Mockito.argThat(new MatchesTeacherStudentIds()))).thenReturn(teacherStudentIds);
        Mockito.when(ttsValidator.validate(Mockito.matches("student"), Mockito.argThat(new MatchesSomeStudentIds()))).thenReturn(someTeacherStudentIds);
        Mockito.when(ttsValidator.validate(Mockito.matches("student"), Mockito.argThat(new MatchesAllStudentIds()))).thenReturn(teacherStudentIds);
        Mockito.when(ttsValidator.getContext()).thenReturn(SecurityUtil.UserContext.TEACHER_CONTEXT);

        AbstractContextValidator ststValidator = Mockito.mock(StaffTeacherToStaffTeacherValidator.class);
        Mockito.when(ststValidator.canValidate(Mockito.matches("staff"), Mockito.anyBoolean())).thenReturn(true);
        Set<String> staffIds = new HashSet<String>(Arrays.asList("staff1"));
        Mockito.when(ststValidator.validate(Mockito.matches("staff"), Mockito.eq(staffIds))).thenReturn(staffIds);
        Mockito.when(ststValidator.getContext()).thenReturn(SecurityUtil.UserContext.DUAL_CONTEXT);

        validators = new ArrayList<IContextValidator>(Arrays.asList(stsValidator, ttsValidator, ststValidator));
        Field field0 = ContextValidator.class.getDeclaredField("validators");
        field0.setAccessible(true);
        field0.set(contextValidator, validators);

        repo = Mockito.mock(PagingRepositoryDelegate.class);
        Field field1 = ContextValidator.class.getDeclaredField("repo");
        field1.setAccessible(true);
        field1.set(contextValidator, repo);

        ownership = Mockito.mock(EntityOwnershipValidator.class);
        Field field2 = ContextValidator.class.getDeclaredField("ownership");
        field2.setAccessible(true);
        field2.set(contextValidator, ownership);

        edOrgHelper = Mockito.mock(EdOrgHelper.class);
        Field field3 = ContextValidator.class.getDeclaredField("edOrgHelper");
        field3.setAccessible(true);
        field3.set(contextValidator, edOrgHelper);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getEntityId()).thenReturn("staff1");
        Mockito.when(entity.getType()).thenReturn("staff");
        SLIPrincipal principal = Mockito.mock(SLIPrincipal.class);
        Mockito.when(principal.isAdminRealmAuthenticated()).thenReturn(false);
        Mockito.when(principal.getEntity()).thenReturn(entity);
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(principal);
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testValidateContextToEntities() {
        EntityDefinition def = createEntityDef("student");

        Entity student1 = createEntity("student", 1);
        Entity student2 = createEntity("student", 2);
        Entity student3 = createEntity("student", 3);
        Entity student5 = createEntity("student", 5);
        Mockito.when(repo.findAll(Mockito.matches("student"), Mockito.any(NeutralQuery.class))).thenReturn(Arrays.asList(student1, student2, student3, student5));

        Mockito.when(edOrgHelper.getDirectEdorgs(Mockito.eq(student1))).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        Mockito.when(edOrgHelper.getDirectEdorgs(Mockito.eq(student2))).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        Mockito.when(edOrgHelper.getDirectEdorgs(Mockito.eq(student3))).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        Mockito.when(edOrgHelper.getDirectEdorgs(Mockito.eq(student5))).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        boolean isTransitive = false;
        Mockito.when(ownership.canAccess(student1, isTransitive)).thenReturn(true);
        Mockito.when(ownership.canAccess(student2, isTransitive)).thenReturn(true);
        Mockito.when(ownership.canAccess(student3, isTransitive)).thenReturn(true);
        Mockito.when(ownership.canAccess(student5, isTransitive)).thenReturn(true);

        Collection<String> ids = new HashSet<String>(Arrays.asList("student1", "student2", "student3", "student5"));

        contextValidator.validateContextToEntities(def, ids, isTransitive);
    }

    @Test
    public void testValidateContextToEntitiesOrphaned() {
        EntityDefinition def = createEntityDef("student");

        Entity student1 = createEntity("student", 1);
        Map<String, Object> metaData = new HashMap<String, Object>();
        metaData.put("createdBy", "staff1");
        metaData.put("isOrphaned", "true");
        Mockito.when(student1.getMetaData()).thenReturn(metaData);
        Mockito.when(repo.findAll(Mockito.matches("student"), Mockito.any(NeutralQuery.class))).thenReturn(Arrays.asList(student1));

        Mockito.when(edOrgHelper.getDirectEdorgs(Mockito.eq(student1))).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        boolean isTransitive = false;

        Collection<String> ids = new HashSet<String>(Arrays.asList("student1"));

        contextValidator.validateContextToEntities(def, ids, isTransitive);

        Mockito.verify(ownership, Mockito.never()).canAccess(Mockito.any(Entity.class), Mockito.anyBoolean());
    }

    @Test
    public void testValidateContextToEntitiesSelf() {
        EntityDefinition def = createEntityDef("staff");

        Entity staff1 = createEntity("staff", 1);
        Mockito.when(repo.findAll(Mockito.matches("staff"), Mockito.any(NeutralQuery.class))).thenReturn(Arrays.asList(staff1));

        Mockito.when(edOrgHelper.getDirectEdorgs(Mockito.eq(staff1))).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        boolean isTransitive = false;

        Collection<String> ids = new HashSet<String>(Arrays.asList("staff1"));

        contextValidator.validateContextToEntities(def, ids, isTransitive);

        Mockito.verify(ownership, Mockito.never()).canAccess(Mockito.any(Entity.class), Mockito.anyBoolean());
    }

    @Test
    public void testValidateContextToEntitiesCannotAccess() {
        EntityDefinition def = createEntityDef("student");

        Entity student1 = createEntity("student", 1);
        Mockito.when(repo.findAll(Mockito.matches("student"), Mockito.any(NeutralQuery.class))).thenReturn(Arrays.asList(student1));

        Mockito.when(edOrgHelper.getDirectEdorgs(Mockito.eq(student1))).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        boolean isTransitive = false;
        Mockito.when(ownership.canAccess(student1, isTransitive)).thenReturn(false);

        Collection<String> ids = new HashSet<String>(Arrays.asList("student1"));

        try {
            contextValidator.validateContextToEntities(def, ids, isTransitive);
            Assert.fail();
        } catch (APIAccessDeniedException ex) {
            Assert.assertEquals("Access to " + student1.getEntityId() + " is not authorized", ex.getMessage());
        }
    }

    @Test
    public void testValidateContextToEntitiesNoEntitiesInDb() {
        EntityDefinition def = createEntityDef("student");

        Entity student1 = createEntity("student", 1);
        Mockito.when(repo.findAll(Mockito.matches("student"), Mockito.any(NeutralQuery.class))).thenReturn(new ArrayList<Entity>());

        Mockito.when(edOrgHelper.getDirectEdorgs(Mockito.eq(student1))).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        boolean isTransitive = false;
        Mockito.when(ownership.canAccess(student1, isTransitive)).thenReturn(true);

        Collection<String> ids = new HashSet<String>(Arrays.asList("student1"));

        try {
            contextValidator.validateContextToEntities(def, ids, isTransitive);
            Assert.fail();
        } catch (EntityNotFoundException ex) {
            Assert.assertEquals("Could not locate " + def.getType() + " with ids " + ids, ex.getId());
        }
    }

    @Test
    public void testValidateContextToEntitiesNotAllValidated() {
        EntityDefinition def = createEntityDef("student");

        Entity student0 = createEntity("student", 0);
        Entity student1 = createEntity("student", 1);
        Entity student2 = createEntity("student", 2);
        Mockito.when(repo.findAll(Mockito.matches("student"), Mockito.any(NeutralQuery.class))).thenReturn(Arrays.asList(student0, student1, student2));

        Mockito.when(edOrgHelper.getDirectEdorgs(Mockito.eq(student1))).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        boolean isTransitive = false;
        Mockito.when(ownership.canAccess(student0, isTransitive)).thenReturn(true);
        Mockito.when(ownership.canAccess(student1, isTransitive)).thenReturn(true);
        Mockito.when(ownership.canAccess(student2, isTransitive)).thenReturn(true);

        Collection<String> ids = new HashSet<String>(Arrays.asList("student0", "student1", "student2"));

        try {
            contextValidator.validateContextToEntities(def, ids, isTransitive);
            Assert.fail();
        } catch (APIAccessDeniedException ex) {
            Assert.assertEquals("Cannot access entities", ex.getMessage());
        }
    }

    @Test
    public void testValidateContextToEntitiesSelfNoValidators() {
        EntityDefinition def = createEntityDef("session");

        Entity session1 = createEntity("session", 1);
        Mockito.when(repo.findAll(Mockito.matches("session"), Mockito.any(NeutralQuery.class))).thenReturn(Arrays.asList(session1));

        Mockito.when(edOrgHelper.getDirectEdorgs(Mockito.eq(session1))).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        boolean isTransitive = false;

        Collection<String> ids = new HashSet<String>(Arrays.asList("session1"));

        try {
            contextValidator.validateContextToEntities(def, ids, isTransitive);
            Assert.fail();
        } catch (APIAccessDeniedException ex) {
            Assert.assertEquals("No validator for " + def.getType() + ", transitive=" + isTransitive, ex.getMessage());
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetValidatedEntityContexts() {
        EntityDefinition def = createEntityDef("student");

        Mockito.when(edOrgHelper.getDirectEdorgs(Mockito.any(Entity.class))).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        boolean isTransitive = false;
        Mockito.when(ownership.canAccess(Mockito.any(Entity.class), Mockito.anyBoolean())).thenReturn(true);

        Entity student1 = createEntity("student", 1);
        Entity student2 = createEntity("student", 2);
        Entity student3 = createEntity("student", 3);
        Entity student4 = createEntity("student", 4);
        Entity student5 = createEntity("student", 5);
        List<Entity> students = Arrays.asList(student1, student2, student3, student4, student5);

        Map<String, SecurityUtil.UserContext> validatedEntityContexts = contextValidator.getValidatedEntityContexts(def, students, isTransitive, true);

        Assert.assertEquals(5, validatedEntityContexts.size());
        Assert.assertEquals(SecurityUtil.UserContext.STAFF_CONTEXT, validatedEntityContexts.get("student1"));
        Assert.assertEquals(SecurityUtil.UserContext.DUAL_CONTEXT, validatedEntityContexts.get("student2"));
        Assert.assertEquals(SecurityUtil.UserContext.STAFF_CONTEXT, validatedEntityContexts.get("student3"));
        Assert.assertEquals(SecurityUtil.UserContext.TEACHER_CONTEXT, validatedEntityContexts.get("student4"));
        Assert.assertEquals(SecurityUtil.UserContext.DUAL_CONTEXT, validatedEntityContexts.get("student5"));
    }

    @Test
    public void testGetValidatedEntityContextsOrphaned() {
        EntityDefinition def = createEntityDef("student");

        Entity student1 = createEntity("student", 1);
        Map<String, Object> metaData = new HashMap<String, Object>();
        metaData.put("createdBy", "staff1");
        metaData.put("isOrphaned", "true");
        Mockito.when(student1.getMetaData()).thenReturn(metaData);
        List<Entity> students = Arrays.asList(student1);

        Mockito.when(edOrgHelper.getDirectEdorgs(Mockito.eq(student1))).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        boolean isTransitive = false;

        Map<String, SecurityUtil.UserContext> validatedEntityContexts = contextValidator.getValidatedEntityContexts(def, students, isTransitive, true);

        Assert.assertEquals(1, validatedEntityContexts.size());
        Assert.assertEquals(SecurityUtil.UserContext.DUAL_CONTEXT, validatedEntityContexts.get("student1"));
        Mockito.verify(ownership, Mockito.never()).canAccess(Mockito.any(Entity.class), Mockito.anyBoolean());
    }

    @SuppressWarnings("unused")
    @Test
    public void testGetValidatedEntityContextsSelf() {
        EntityDefinition def = createEntityDef("staff");

        Entity staff1 = createEntity("staff", 1);
        List<Entity> staff = Arrays.asList(staff1);

        Mockito.when(edOrgHelper.getDirectEdorgs(Mockito.eq(staff1))).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        boolean isTransitive = false;

        Map<String, SecurityUtil.UserContext> validatedEntityContexts = contextValidator.getValidatedEntityContexts(def, staff, isTransitive, true);

        Mockito.verify(ownership, Mockito.never()).canAccess(Mockito.any(Entity.class), Mockito.anyBoolean());
    }

    @SuppressWarnings("unused")
    @Test
    public void testGetValidatedEntityContextsCannotAccess() {
        EntityDefinition def = createEntityDef("student");

        Entity student1 = createEntity("student", 1);
        List<Entity> students = Arrays.asList(student1);

        Mockito.when(edOrgHelper.getDirectEdorgs(Mockito.eq(student1))).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        boolean isTransitive = false;
        Mockito.when(ownership.canAccess(student1, isTransitive)).thenReturn(false);

        Map<String, SecurityUtil.UserContext> validatedEntityContexts = contextValidator.getValidatedEntityContexts(def, students, isTransitive, true);
        Assert.assertEquals(1, validatedEntityContexts.size());

    }

    @SuppressWarnings("unused")
    @Test
    public void testGetValidatedEntityContextsNoEntitiesInList() {
        EntityDefinition def = createEntityDef("student");

        Entity student1 = createEntity("student", 1);

        Mockito.when(edOrgHelper.getDirectEdorgs(Mockito.eq(student1))).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        boolean isTransitive = false;
        Mockito.when(ownership.canAccess(student1, isTransitive)).thenReturn(true);

        Collection<String> ids = new HashSet<String>();

        Map<String, SecurityUtil.UserContext> validatedEntityContexts = contextValidator.getValidatedEntityContexts(def, new ArrayList<Entity>(), isTransitive, true);

        Assert.assertTrue(validatedEntityContexts.isEmpty());
    }

    @Test
    public void testGetValidatedEntityContextsNotAllValidated() {
        EntityDefinition def = createEntityDef("student");

        Entity student0 = createEntity("student", 0);
        Entity student1 = createEntity("student", 1);
        Entity student2 = createEntity("student", 2);
        List<Entity> students = Arrays.asList(student0, student1, student2);

        Mockito.when(edOrgHelper.getDirectEdorgs(Mockito.eq(student1))).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        boolean isTransitive = false;
        Mockito.when(ownership.canAccess(student0, isTransitive)).thenReturn(true);
        Mockito.when(ownership.canAccess(student1, isTransitive)).thenReturn(true);
        Mockito.when(ownership.canAccess(student2, isTransitive)).thenReturn(true);

        Map<String, SecurityUtil.UserContext> validatedEntityContexts = contextValidator.getValidatedEntityContexts(def, students, isTransitive, true);

        Assert.assertEquals(2, validatedEntityContexts.size());
        Assert.assertEquals(SecurityUtil.UserContext.STAFF_CONTEXT, validatedEntityContexts.get("student1"));
        Assert.assertEquals(SecurityUtil.UserContext.DUAL_CONTEXT, validatedEntityContexts.get("student2"));
    }

    @SuppressWarnings("unused")
    @Test
    public void testGetValidatedEntityContextsNoValidators() {
        EntityDefinition def = createEntityDef("session");

        Entity session1 = createEntity("session", 1);
        List<Entity> sessions = Arrays.asList(session1);

        Mockito.when(edOrgHelper.getDirectEdorgs(Mockito.eq(session1))).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        boolean isTransitive = false;

        try {
            Map<String, SecurityUtil.UserContext> validatedEntityContexts = contextValidator.getValidatedEntityContexts(def, sessions, isTransitive, true);
            Assert.fail();
        } catch (APIAccessDeniedException ex) {
            Assert.assertEquals("No validator for " + def.getType() + ", transitive=" + isTransitive, ex.getMessage());
        }
    }

    @Test
    public void testgetEntityIdsToValidateForgiving() {
        Entity student0 = createEntity("student", 0);
        Entity student1 = createEntity("student", 1);
        Entity student2 = createEntity("student", 2);
        List<Entity> students = Arrays.asList(student0, student1, student2);


        boolean isTransitive = false;
        Mockito.when(ownership.canAccess(student0, isTransitive)).thenReturn(true);
        Mockito.when(ownership.canAccess(student1, isTransitive)).thenReturn(true);
        Mockito.when(ownership.canAccess(student2, isTransitive)).thenThrow(new AccessDeniedException(""));

        Set<String> ids = contextValidator.getEntityIdsToValidateForgiving(students, isTransitive);

        Assert.assertEquals(2, ids.size());
        Assert.assertTrue(ids.contains(student0.getEntityId()));
        Assert.assertTrue(ids.contains(student1.getEntityId()));
    }

    public void testGetGlobalEntities() {
        EntityDefinition def = createEntityDef("calendarDate");
        Mockito.when(def.getResourceName()).thenReturn(ResourceNames.CALENDAR_DATES);

        Entity session1 = createEntity("calendarDate", 1);
        List<Entity> cd = Arrays.asList(session1);

        boolean isTransitive = false;

        try {
            Map<String, SecurityUtil.UserContext> validatedEntityContexts = contextValidator.getValidatedEntityContexts(def, cd, isTransitive, true);
            Assert.assertEquals("Return should be null", null, validatedEntityContexts);
        } catch (APIAccessDeniedException ex) {
            Assert.assertEquals("No validator for " + def.getType() + ", transitive=" + isTransitive, ex.getMessage());
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Entity createEntity(String type, int num) {
        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn(type);
        Mockito.when(entity.getEntityId()).thenReturn(type + num);

        return entity;
    }

    private EntityDefinition createEntityDef(String type) {
        EntityDefinition entityDef = Mockito.mock(EntityDefinition.class);
        Mockito.when(entityDef.getType()).thenReturn(type);
        Mockito.when(entityDef.getStoredCollectionName()).thenReturn(type);

        return entityDef;
    }

    @AfterClass
    public static void reset() {
        // Let's be good citizens and clean up after ourselves for the subsequent tests!
        SecurityUtil.setUserContext(prevUserContext);
        SecurityContextHolder.setContext(prevSecurityContext);
    }
}
