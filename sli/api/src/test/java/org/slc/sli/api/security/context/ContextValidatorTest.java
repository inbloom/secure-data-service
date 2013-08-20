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
import org.mockito.Mockito;
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

    @SuppressWarnings("unchecked")
    @Before
    public void setup() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        contextValidator = new ContextValidator();

        SecurityUtil.setUserContext(SecurityUtil.UserContext.STAFF_CONTEXT);

        AbstractContextValidator stsValidator = Mockito.mock(StaffToStudentValidator.class);
        Mockito.when(stsValidator.canValidate(Mockito.matches("student"), Mockito.anyBoolean())).thenReturn(true);
        Set<String> studentIds = new HashSet<String>(Arrays.asList("student1"));
        Mockito.when(stsValidator.validate(Mockito.matches("student"), Mockito.eq(studentIds))).thenReturn(studentIds);
        AbstractContextValidator ststValidator = Mockito.mock(StaffTeacherToStaffTeacherValidator.class);
        Mockito.when(ststValidator.canValidate(Mockito.matches("staff"), Mockito.anyBoolean())).thenReturn(true);
        Set<String> staffIds = new HashSet<String>(Arrays.asList("staff1"));
        Mockito.when(ststValidator.validate(Mockito.matches("staff"), Mockito.eq(studentIds))).thenReturn(staffIds);
        validators = new ArrayList<IContextValidator>(Arrays.asList(stsValidator, ststValidator));
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

    @Test
    public void testValidateContextToEntities() {
        EntityDefinition def = Mockito.mock(EntityDefinition.class);
        Mockito.when(def.getType()).thenReturn("student");
        Mockito.when(def.getStoredCollectionName()).thenReturn("student");

        Entity ent = Mockito.mock(Entity.class);
        Mockito.when(ent.getEntityId()).thenReturn("student1");
        Mockito.when(ent.getType()).thenReturn("student");
        Mockito.when(repo.findAll(Mockito.matches("student"), Mockito.any(NeutralQuery.class))).thenReturn(Arrays.asList(ent));

        Mockito.when(edOrgHelper.getDirectEdorgs(Mockito.eq(ent))).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        boolean isTransitive = false;
        Mockito.when(ownership.canAccess(ent, isTransitive)).thenReturn(true);

        Collection<String> ids = new HashSet<String>(Arrays.asList("student1"));

        contextValidator.validateContextToEntities(def, ids, isTransitive);
    }

    @Test
    public void testValidateContextToEntitiesOrphaned() {
        EntityDefinition def = Mockito.mock(EntityDefinition.class);
        Mockito.when(def.getType()).thenReturn("student");
        Mockito.when(def.getStoredCollectionName()).thenReturn("student");

        Entity ent = Mockito.mock(Entity.class);
        Mockito.when(ent.getEntityId()).thenReturn("student1");
        Mockito.when(ent.getType()).thenReturn("student");
        Map<String, Object> metaData = new HashMap<String, Object>();
        metaData.put("createdBy", "staff1");
        metaData.put("isOrphaned", "true");
        Mockito.when(ent.getMetaData()).thenReturn(metaData);
        Mockito.when(repo.findAll(Mockito.matches("student"), Mockito.any(NeutralQuery.class))).thenReturn(Arrays.asList(ent));

        Mockito.when(edOrgHelper.getDirectEdorgs(Mockito.eq(ent))).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        boolean isTransitive = false;

        Collection<String> ids = new HashSet<String>(Arrays.asList("student1"));

        contextValidator.validateContextToEntities(def, ids, isTransitive);

        Mockito.verify(ownership, Mockito.never()).canAccess(Mockito.any(Entity.class), Mockito.anyBoolean());
    }

    @Test
    public void testValidateContextToEntitiesSelf() {
        EntityDefinition def = Mockito.mock(EntityDefinition.class);
        Mockito.when(def.getType()).thenReturn("staff");
        Mockito.when(def.getStoredCollectionName()).thenReturn("staff");

        Entity ent = Mockito.mock(Entity.class);
        Mockito.when(ent.getEntityId()).thenReturn("staff1");
        Mockito.when(ent.getType()).thenReturn("staff");
        Mockito.when(repo.findAll(Mockito.matches("staff"), Mockito.any(NeutralQuery.class))).thenReturn(Arrays.asList(ent));

        Mockito.when(edOrgHelper.getDirectEdorgs(Mockito.eq(ent))).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        boolean isTransitive = false;

        Collection<String> ids = new HashSet<String>(Arrays.asList("staff1"));

        contextValidator.validateContextToEntities(def, ids, isTransitive);

        Mockito.verify(ownership, Mockito.never()).canAccess(Mockito.any(Entity.class), Mockito.anyBoolean());
    }

    @Test
    public void testValidateContextToEntitiesCannotAccess() {
        EntityDefinition def = Mockito.mock(EntityDefinition.class);
        Mockito.when(def.getType()).thenReturn("student");
        Mockito.when(def.getStoredCollectionName()).thenReturn("student");

        Entity ent = Mockito.mock(Entity.class);
        Mockito.when(ent.getEntityId()).thenReturn("student1");
        Mockito.when(ent.getType()).thenReturn("student");
        Mockito.when(repo.findAll(Mockito.matches("student"), Mockito.any(NeutralQuery.class))).thenReturn(Arrays.asList(ent));

        Mockito.when(edOrgHelper.getDirectEdorgs(Mockito.eq(ent))).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        boolean isTransitive = false;
        Mockito.when(ownership.canAccess(ent, isTransitive)).thenReturn(false);

        Collection<String> ids = new HashSet<String>(Arrays.asList("student1"));

        try {
            contextValidator.validateContextToEntities(def, ids, isTransitive);
            Assert.fail();
        } catch (APIAccessDeniedException ex) {
            Assert.assertEquals("Access to " + ent.getEntityId() + " is not authorized", ex.getMessage());
        }
    }

    @Test
    public void testValidateContextToEntitiesNoEntitiesInDb() {
        EntityDefinition def = Mockito.mock(EntityDefinition.class);
        Mockito.when(def.getType()).thenReturn("student");
        Mockito.when(def.getStoredCollectionName()).thenReturn("student");

        Entity ent = Mockito.mock(Entity.class);
        Mockito.when(ent.getEntityId()).thenReturn("student1");
        Mockito.when(ent.getType()).thenReturn("student");
        Mockito.when(repo.findAll(Mockito.matches("student"), Mockito.any(NeutralQuery.class))).thenReturn(new ArrayList<Entity>());

        Mockito.when(edOrgHelper.getDirectEdorgs(Mockito.eq(ent))).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        boolean isTransitive = false;
        Mockito.when(ownership.canAccess(ent, isTransitive)).thenReturn(false);

        Collection<String> ids = new HashSet<String>(Arrays.asList("student1"));

        try {
            contextValidator.validateContextToEntities(def, ids, isTransitive);
            Assert.fail();
        } catch (EntityNotFoundException ex) {
            Assert.assertEquals("Could not locate " + def.getType() + " with ids " + ids, ex.getId());
        }
    }

    @Test
    public void testValidateContextToEntitiesSelfNoValidators() {
        EntityDefinition def = Mockito.mock(EntityDefinition.class);
        Mockito.when(def.getType()).thenReturn("session");
        Mockito.when(def.getStoredCollectionName()).thenReturn("session");

        Entity ent = Mockito.mock(Entity.class);
        Mockito.when(ent.getEntityId()).thenReturn("session1");
        Mockito.when(ent.getType()).thenReturn("session");
        Mockito.when(repo.findAll(Mockito.matches("session"), Mockito.any(NeutralQuery.class))).thenReturn(Arrays.asList(ent));

        Mockito.when(edOrgHelper.getDirectEdorgs(Mockito.eq(ent))).thenReturn(new HashSet<String>(Arrays.asList("edOrg1")));
        boolean isTransitive = false;

        Collection<String> ids = new HashSet<String>(Arrays.asList("session1"));

        try {
            contextValidator.validateContextToEntities(def, ids, isTransitive);
            Assert.fail();
        } catch (APIAccessDeniedException ex) {
            Assert.assertEquals("No validator for " + def.getType() + ", transitive=" + isTransitive, ex.getMessage());
        }
    }

    @AfterClass
    public static void reset() {
        // Let's be good citizens and clean up after ourselves for the subsequent tests!
        SecurityUtil.setUserContext(prevUserContext);
        SecurityContextHolder.setContext(prevSecurityContext);
    }
}
